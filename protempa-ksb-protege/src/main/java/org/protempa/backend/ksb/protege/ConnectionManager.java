/*
 * #%L
 * Protempa Protege Knowledge Source Backend
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.ksb.protege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import org.protempa.backend.KnowledgeSourceBackendInitializationException;
import org.protempa.KnowledgeSourceReadException;

/**
 * Implements a wrapper around a connection to a remote Protege project.
 * Implements retry capability if something happens to the connection.
 * 
 * @author Andrew Post
 * @param <T>
 */
abstract class ConnectionManager {

    /**
     * Number of times we try to execute a command.
     */
    private static final int TRIES = 3;
    private final String projectIdentifier;
    private Project project;
    private KnowledgeBase protegeKnowledgeBase;
    private List<ProjectListener> projectListeners;

    /**
     * Constructor with an identifier for the Protege project.
     *
     * @param projectIdentifier
     *      a name {@link String} for a knowledge base, cannot be
     *      <code>null</code>.
     */
    protected ConnectionManager(String projectIdentifier) {
        if (projectIdentifier == null) {
            throw new IllegalArgumentException(
                    "projectIdentifier cannot be null");
        }
        this.projectIdentifier = projectIdentifier;
        this.projectListeners = new ArrayList<ProjectListener>();
    }

    /**
     * Returns the project identifier.
     *
     * @return a {@link String} guaranteed not to be <code>null</code>.
     */
    String getProjectIdentifier() {
        return this.projectIdentifier;
    }

    /**
     * Opens the project.
     *
     * @throws KnowledgeSourceBackendInitializationException if an error
     * occurs.
     */
    void init() throws KnowledgeSourceBackendInitializationException {
        if (this.project == null) {
            Util.logger().log(Level.FINE, "Opening Protege project {0}", 
                    this.projectIdentifier);
            this.project = initProject();
            if (this.project == null) {
                throw new KnowledgeSourceBackendInitializationException(
                        "Could not load project " + this.projectIdentifier);
            } else {
                this.protegeKnowledgeBase = this.project.getKnowledgeBase();
                Util.logger().log(Level.FINE, 
                        "Project {0} opened successfully",
                        this.projectIdentifier);
            }
        }
    }

    /**
     * Opens and returns the project in a way that is specific to whether
     * the project is local or remote.
     *
     * @return a Protege {@link Project}.
     */
    protected abstract Project initProject();

    /**
     * Closes the project.
     */
    void close() {
        if (this.project != null) {
            Util.logger().log(Level.FINE, "Closing Protege project {0}",
                    this.projectIdentifier);

            for (Iterator<ProjectListener> itr = 
                    this.projectListeners.iterator(); itr.hasNext();) {
                this.project.removeProjectListener(itr.next());
                itr.remove();
            }
            try {
                this.project.dispose();
                Util.logger().fine("Done closing connection.");
            } catch (IllegalStateException e) {
                /*
                 * Protege registers a shutdown hook that is removed when
                 * dispose() is called on a project. However, we might call this
                 * method during an already started shutdown process. See
                 * documentation for java.lang.Runtime.removeShutdownHook. This
                 * exception should be harmless, unless there are other reasons
                 * dispose() could throw this exception?
                 */
                Util.logger().fine("Done closing connection.");
            } finally {
                this.project = null;
            }

        }
    }

    /**
     * Adds a listener for changes in the project.
     *
     * @param listener
     *            a <code>ProjectListener</code>.
     */
    void addProjectListener(ProjectListener listener) {
        if (listener != null) {
            this.projectListeners.add(listener);
        }
    }

    /**
     * Removes a listener for changes in the project.
     *
     * @param listener
     *            a <code>ProjectListener</code>.
     */
    void removeProjectListener(ProjectListener listener) {
        this.project.removeProjectListener(listener);
        this.projectListeners.remove(listener);
    }

    /*
     * GETTERS FOR THE PROJECT'S KNOWLEDGE BASE, WITH RETRY SUPPORT.
     */
    /**
     * Template for executing various commands upon the project's knowledge base.
     *
     * @author Andrew Post
     *
     * @param <S>
     *            The type of what is returned by the getter.
     * @param <T>
     *            The type of what is passed to Protege as a parameter.
     */
    private abstract class ProtegeCommand<S, T> {

        private String what;

        /**
         * Constructor.
         *
         * @param what
         *            what the command is doing, used for debugging.
         */
        ProtegeCommand(String what) {
            this.what = what;
        }

        /**
         * Implement this with the protege command. This may assume that
         * <code>protegeKnowledgeBase</code> is not <code>null</code>.
         *
         * @param obj
         *            the parameter to send to Protege.
         * @return the object returned from Protege.
         */
        abstract S get(T obj);

        /**
         * Does <code>null</code> checks on <code>protegeKnowledgeBase</code>
         * and <code>obj</code> before calling the {@link #get(Object)}
         * method.
         *
         * @param obj
         *            the parameter to pass to the Protege command.
         * @return the object returned from Protege, or <code>null</code> if
         *         <code>protegeKnowledgeBase</code> or <code>obj</code> is
         *         <code>null</code>, or if <code>null</code> is returned
         *         from the Protege command.
         */
        final S getHelper(T obj) {
            if (protegeKnowledgeBase != null && obj != null) {
                return get(obj);
            } else {
                return null;
            }
        }

        /**
         * Returns a string representing what this command is doing.
         *
         * @return a <code>String</code>.
         */
        final String getWhat() {
            return what;
        }
    }

    /**
     * Executes a command upon the Protege knowledge base, retrying if needed.
     *
     * @param <S>
     *            The type of what is returned by the getter.
     * @param <T>
     *            The type of what is passed to protege as a parameter.
     * @param obj
     *            What is passed to Protege as a parameter.
     * @param getter
     *            the <code>ProtegeCommand</code>.
     * @return what is returned from the Protege command.
     */
    private <S, T> S getFromProtege(T obj, ProtegeCommand<S, T> getter)
            throws KnowledgeSourceReadException {
        if (protegeKnowledgeBase != null && getter != null) {
            int tries = TRIES;
            Exception lastException = null;
            do {
                try {
                    return getter.getHelper(obj);
                } catch (Exception e) {
                    lastException = e;
                    Util.logger().log(
                            Level.WARNING,
                            "Exception attempting to "
                            + getter.getWhat() + " " + obj, e);
                    tries--;
                }
                close();
                try {
                    init();
                } catch (KnowledgeSourceBackendInitializationException e) {
                    throw new KnowledgeSourceReadException(
                            "Exception attempting to "
                            + getter.getWhat() + " " + obj, e);
                }
            } while (tries > 0);
            throw new KnowledgeSourceReadException(
                    "Failed to " + getter.getWhat() + " "
                    + obj + " after "
                    + TRIES + " tries", lastException);
        }
        return null;
    }
    /**
     * Command for retrieving instances from the knowledge base.
     *
     * @see #getInstance(String)
     */
    private final ProtegeCommand<Instance, String> INSTANCE_GETTER =
            new ProtegeCommand<Instance, String>("get instance") {

                /**
                 * Gets a specified instance from the knowledge base.
                 *
                 * @param name
                 *            the instance's name <code>String</code>.
                 * @return the <code>Instance</code>, or <code>null</code> if no
                 *         instance with that name was found.
                 * @see ConnectionManager.ProtegeCommand#get(java.lang.Object)
                 */
                @Override
                Instance get(String name) {
                    return protegeKnowledgeBase.getInstance(name);
                }
            };

    /**
     * Retrieves a specified instance from the knowledge base.
     *
     * @param name
     *            the instance's name <code>String</code>.
     * @return the <code>Instance</code>, or <code>null</code> if not
     *         found.
     * @see ConnectionManager#INSTANCE_GETTER
     */
    Instance getInstance(String name) throws KnowledgeSourceReadException {
        return getFromProtege(name, INSTANCE_GETTER);
    }
    /**
     * Command for retrieving instances of a cls from the knowledge base.
     *
     * @see #getInstances(Cls)
     */
    private final ProtegeCommand<Collection<Instance>, Cls> INSTANCES_GETTER =
            new ProtegeCommand<Collection<Instance>, Cls>("get instances") {

                /**
                 * Gets all instances of a specified cls from the knowledge base.
                 *
                 * @param cls
                 *            the <code>Cls</code>.
                 * @return a <code>Collection</code> of <code>Instance</code>s.
                 *         Guaranteed not to be <code>null</code>.
                 * @see ConnectionManager.ProtegeCommand#get(java.lang.Object)
                 */
                @Override
                Collection<Instance> get(Cls cls) {
                    Collection<Instance> result = protegeKnowledgeBase.getInstances(cls);
                    // Protect against Protege returning null (its API doesn't guarantee
                    // a non-null return value).
                    if (result != null) {
                        return result;
                    } else {
                        return new ArrayList<Instance>(0);
                    }
                }
            };

    /**
     * Gets all instances of the specified cls from the knowledge base.
     *
     * @param cls
     *            a <code>Cls</code>
     * @return a <code>Collection</code> of <code>Instance</code>s.
     *         Guaranteed not to be <code>null</code>.
     * @see ConnectionManager#INSTANCES_GETTER
     */
    Collection<Instance> getInstances(Cls cls)
            throws KnowledgeSourceReadException {
        return getFromProtege(cls, INSTANCES_GETTER);
    }
    /**
     * Command for getting clses from the knowledge base.
     *
     * @see #getCls(String)
     */
    private final ProtegeCommand<Cls, String> CLS_GETTER =
            new ProtegeCommand<Cls, String>("get cls") {

                /**
                 * Gets a cls from the knowledge base.
                 *
                 * @param name
                 *            the cls's name <code>String</code>.
                 * @return the <code>Cls</code>, or <code>null</code> if no cls
                 *         with that name was found.
                 * @see ConnectionManager.ProtegeCommand#get(java.lang.Object)
                 */
                @Override
                Cls get(String name) {
                    return protegeKnowledgeBase.getCls(name);
                }
            };

    /**
     * Gets the specified cls from the knowledge base.
     *
     * @param name
     *            the name <code>String</code> of the cls.
     * @return the <code>Cls</code>, or <code>null</code> if no cls with
     *         that name was found.
     * @see ConnectionManager#CLS_GETTER
     */
    Cls getCls(String name) throws KnowledgeSourceReadException {
        return getFromProtege(name, CLS_GETTER);
    }
    /**
     * Command for getting slots from the knowledge base.
     *
     * @see #getSlot(String)
     */
    private final ProtegeCommand<Slot, String> SLOT_GETTER =
            new ProtegeCommand<Slot, String>("get slot") {

                /**
                 * Gets a slot from the knowledge base.
                 *
                 * @param name
                 *            the slot's name <code>String</code>.
                 * @return the <code>Slot</code>, or <code>null</code> if no slot
                 *         with that name was found.
                 * @see ConnectionManager.ProtegeCommand#get(java.lang.Object)
                 */
                @Override
                Slot get(String name) {
                    return protegeKnowledgeBase.getSlot(name);
                }
            };

    /**
     * Gets the specified slot from the knowledge base.
     *
     * @param name
     *            the name <code>String</code> of the slot.
     * @return the <code>Slot</code>, or <code>null</code> if no slot with
     *         that name was found.
     * @see ConnectionManager#SLOT_GETTER
     */
    Slot getSlot(String name) throws KnowledgeSourceReadException {
        return getFromProtege(name, SLOT_GETTER);
    }

    /**
     * Container for passing parameters for creating Protege instances.
     *
     * @author Andrew Post
     * @see ConnectionManager#INSTANCE_CREATOR
     */
    private static final class InstanceSpec {

        /**
         * The name <code>String</code> of the instance.
         */
        String name;
        /**
         * The <code>Cls</code> of the instance.
         */
        Cls cls;

        /**
         * @param name
         *            the name <code>String</code> of the instance.
         * @param cls
         *            the <code>Cls</code> of the instance.
         */
        InstanceSpec(String name, Cls cls) {
            this.name = name;
            this.cls = cls;
        }
    }
    /**
     * Command for creating new instances.
     *
     * @see #createInstance(String, Cls)
     */
    private final ProtegeCommand<Instance, InstanceSpec> INSTANCE_CREATOR =
            new ProtegeCommand<Instance, InstanceSpec>("create instance") {

                /**
                 * Creates a new instance in the knowledge base.
                 *
                 * @param instanceSpec
                 *            a {@link ConnectionManager.InstanceSpec} specifying the
                 *            instance to create.
                 * @return the created <code>Instance</code>, or <code>null</code>
                 *         if an invalid class specification was provided.
                 * @see ConnectionManager.ProtegeCommand#get(java.lang.Object)
                 */
                @Override
                Instance get(InstanceSpec instanceSpec) {
                    return protegeKnowledgeBase.createInstance(instanceSpec.name,
                            instanceSpec.cls);
                }
            };

    /**
     * Creates a new instance in the knowledge base.
     *
     * @param name
     *            the name <code>String</code> of the instance.
     * @param cls
     *            the <code>Cls</code> of the instance.
     * @return a new <code>Instance</code>, or <code>null</code> if an
     *         invalid class specification was provided.
     * @see ConnectionManager#INSTANCE_CREATOR
     */
    Instance createInstance(String name, Cls cls)
            throws KnowledgeSourceReadException {
        return getFromProtege(new InstanceSpec(name, cls), INSTANCE_CREATOR);
    }
    /**
     * Command for deleting an instance from the knowledge base.
     *
     * @see #deleteInstance(Instance)
     */
    private final ProtegeCommand<Instance, Instance> INSTANCE_DELETER =
            new ProtegeCommand<Instance, Instance>("delete instance") {

                /**
                 * Deletes an instance from the knowledge base.
                 *
                 * @param instance
                 *            the <code>Instance</code> to delete.
                 * @return the deleted <code>Instance</code>.
                 * @see ConnectionManager.ProtegeCommand#get(java.lang.Object)
                 */
                @Override
                Instance get(Instance instance) {
                    protegeKnowledgeBase.deleteInstance(instance);
                    return instance;
                }
            };

    /**
     * Deletes an instance from the knowledge base.
     *
     * @param instance
     *            an <code>Instance</code>.
     * @see ConnectionManager#INSTANCE_DELETER
     */
    void deleteInstance(Instance instance) throws KnowledgeSourceReadException {
        getFromProtege(instance, INSTANCE_DELETER);
    }

    private static final class ClsSpec {

        String name;
        Collection<Cls> clses;

        /**
         * @param name
         * @param clses
         */
        ClsSpec(String name, Collection<Cls> clses) {
            this.name = name;
            this.clses = clses;
        }
    }
    private final ProtegeCommand<Cls, ClsSpec> CLS_CREATOR =
            new ProtegeCommand<Cls, ClsSpec>("create cls") {

                @Override
                Cls get(ClsSpec clsSpec) {
                    return protegeKnowledgeBase.createCls(clsSpec.name, clsSpec.clses);
                }
            };

    <E extends Collection<Cls>>     Cls createCls(String name, E clses)
            throws KnowledgeSourceReadException {
        return getFromProtege(new ClsSpec(name, clses), CLS_CREATOR);
    }

    /**
     * Container for passing parameters for creating Protege instances.
     *
     * @author Andrew Post
     * @see ConnectionManager#INSTANCE_CREATOR
     */
    private static final class InstanceSpecMultipleInheritance {

        /**
         * The name <code>String</code> of the instance.
         */
        String name;
        /**
         * The <code>Cls</code> of the instance.
         */
        Collection<Cls> clses;

        /**
         * @param name
         *            the name <code>String</code> of the instance.
         * @param clses
         *            the <code>Cls</code> of the instance.
         */
        InstanceSpecMultipleInheritance(String name, Collection<Cls> clses) {
            this.name = name;
            this.clses = clses;
        }
    }
    private final ProtegeCommand<Instance, InstanceSpecMultipleInheritance> INSTANCE_CREATOR_MULTIPLE_INHERITANCE =
            new ProtegeCommand<Instance, InstanceSpecMultipleInheritance>(
            "create instance multiple inheritance") {

                @Override
                Instance get(InstanceSpecMultipleInheritance clsSpec) {
                    return protegeKnowledgeBase.createCls(clsSpec.name, clsSpec.clses);
                }
            };

    <E extends Collection<Cls>>     Instance createInstance(String name, E clses)
            throws KnowledgeSourceReadException {
        return getFromProtege(new InstanceSpecMultipleInheritance(name, clses),
                INSTANCE_CREATOR_MULTIPLE_INHERITANCE);
    }

    private static class SlotValueSpec {

        Frame frame;
        Slot slot;

        /**
         * @param frame
         * @param slot
         */
        SlotValueSpec(Frame frame, Slot slot) {
            this.frame = frame;
            this.slot = slot;
        }
    }
    private final ProtegeCommand<Object, SlotValueSpec> OWN_SLOT_VALUE_GETTER =
            new ProtegeCommand<Object, SlotValueSpec>("get own slot value") {

                @Override
                Object get(SlotValueSpec slotValueSpec) {
                    return protegeKnowledgeBase.getOwnSlotValue(slotValueSpec.frame,
                            slotValueSpec.slot);
                }
            };

    Object getOwnSlotValue(Frame frame, Slot slot)
            throws KnowledgeSourceReadException {
        return getFromProtege(new SlotValueSpec(frame, slot),
                OWN_SLOT_VALUE_GETTER);
    }
    private final ProtegeCommand<Collection<?>, SlotValueSpec> OWN_SLOT_VALUES_GETTER = new ProtegeCommand<Collection<?>, SlotValueSpec>("get own slot values") {

        @Override
        Collection get(SlotValueSpec slotValueSpec) {
            return protegeKnowledgeBase.getOwnSlotValues(slotValueSpec.frame,
                    slotValueSpec.slot);
        }
    };

    Collection<?> getOwnSlotValues(Frame frame, Slot slot)
            throws KnowledgeSourceReadException {
        return getFromProtege(new SlotValueSpec(frame, slot),
                OWN_SLOT_VALUES_GETTER);
    }
    
    Collection<?> getOwnSlotValues(Frame frame, String slotName) 
            throws KnowledgeSourceReadException {
        return getOwnSlotValues(frame, getSlot(slotName));
    }
    private final ProtegeCommand<Collection<Cls>, Instance> DIRECT_TYPES_GETTER = new ProtegeCommand<Collection<Cls>, Instance>(
            "get direct types") {

        @SuppressWarnings("unchecked")
        @Override
        Collection<Cls> get(Instance instance) {
            return protegeKnowledgeBase.getDirectTypes(instance);
        }
    };

    Collection<Cls> getDirectTypes(Instance instance)
            throws KnowledgeSourceReadException {
        return getFromProtege(instance, DIRECT_TYPES_GETTER);
    }

    private static final class SetSlotValueSpec extends SlotValueSpec {

        Object value;

        SetSlotValueSpec(Frame frame, Slot slot, Object value) {
            super(frame, slot);
            this.value = value;
        }
    }
    private final ProtegeCommand<Object, SetSlotValueSpec> OWN_SLOT_VALUE_SETTER = new ProtegeCommand<Object, SetSlotValueSpec>("set own slot value") {

        @Override
        Object get(SetSlotValueSpec setSlotValueSpec) {
            setSlotValueSpec.frame.setOwnSlotValue(setSlotValueSpec.slot,
                    setSlotValueSpec.value);
            return null;
        }
    };

    void setOwnSlotValue(Instance instance, Slot slot, Object value)
            throws KnowledgeSourceReadException {
        getFromProtege(new SetSlotValueSpec(instance, slot, value),
                OWN_SLOT_VALUE_SETTER);
    }

    private static final class SetSlotValuesSpec extends SlotValueSpec {

        Collection<?> value;

        SetSlotValuesSpec(Frame frame, Slot slot, Collection<?> value) {
            super(frame, slot);
            this.value = value;
        }
    }
    private final ProtegeCommand<Object, SetSlotValuesSpec> OWN_SLOT_VALUES_SETTER = new ProtegeCommand<Object, SetSlotValuesSpec>("set own slot value") {

        @Override
        Object get(SetSlotValuesSpec setSlotValueSpec) {
            setSlotValueSpec.frame.setOwnSlotValues(setSlotValueSpec.slot,
                    setSlotValueSpec.value);
            return null;
        }
    };

    void setOwnSlotValues(Instance instance, Slot slot, Collection<?> values)
            throws KnowledgeSourceReadException {
        getFromProtege(new SetSlotValuesSpec(instance, slot, values),
                OWN_SLOT_VALUES_SETTER);
    }

    private static final class HasTypeSpec {

        Instance instance;
        Cls type;

        /**
         * @param instance
         * @param type
         */
        HasTypeSpec(Instance instance, Cls type) {
            this.instance = instance;
            this.type = type;
        }
    }
    private final ProtegeCommand<Boolean, HasTypeSpec> HAS_TYPE_GETTER =
            new ProtegeCommand<Boolean, HasTypeSpec>(
            "has type") {

                @Override
                Boolean get(HasTypeSpec hasTypeSpec) {
                    return protegeKnowledgeBase.hasType(hasTypeSpec.instance,
                            hasTypeSpec.type);
                }
            };

    boolean hasType(Instance instance, Cls type)
            throws KnowledgeSourceReadException {
        return getFromProtege(new HasTypeSpec(instance, type), HAS_TYPE_GETTER);
    }
    private final ProtegeCommand<String, Frame> NAME_GETTER =
            new ProtegeCommand<String, Frame>("get name") {

                @Override
                String get(Frame obj) {
                    return protegeKnowledgeBase.getName(obj);
                }
            };

    String getName(Frame frame) throws KnowledgeSourceReadException {
        return getFromProtege(frame, NAME_GETTER);
    }
}
