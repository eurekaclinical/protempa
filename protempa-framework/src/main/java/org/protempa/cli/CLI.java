package org.protempa.cli;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.protempa.Protempa;
import org.protempa.ProtempaStartupException;

/**
 * Convenience class for creating command line applications using PROTEMPA.
 * 
 * @author Andrew Post
 */
public abstract class CLI {

    /**
     * For defining command line arguments that are not prefixed by the option
     * syntax.
     */
    public static final class Argument {

        private final String name;
        private final boolean required;

        /**
         * Instantiates an instance with the name of the argument and
         * whether or not it is required.
         *
         * @param name the argument's name, a {@link String}.
         * @param required <code>true</code> if the argument is required,
         * <code>false</code> if not.
         */
        public Argument(String name, boolean required) {
            if (name == null || name.trim().length() == 0) {
                throw new IllegalArgumentException(
                        "name cannot be null or empty");
            }
            this.name = name;
            this.required = required;
        }

        /**
         * Returns the argument's name.
         *
         * @return a {@link String}.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Returns whether the argument is required.
         *
         * @return <code>true</code> if the argument is required,
         * <code>false</code> if not.
         */
        public boolean isRequired() {
            return this.required;
        }

        /**
         * Gets the argument formatted as a string suitable for a usage
         * statement.
         *
         * @return a {@link String}.
         */
        public String getFormatted() {
            if (this.required) {
                return this.name;
            } else {
                return '[' + this.name + ']';
            }
        }
    }
    private Protempa protempa;
    private CommandLine commandLine;
    private final String shellCommand;
    private final Argument[] arguments;

    /**
     * Instantiates the application with a required shell command name.
     *
     * @param shellCommand a {@link String}, cannot be <code>null</code> or
     * empty.
     */
    protected CLI(String shellCommand) {
        this(shellCommand, null);
    }

    /**
     * Instantiates the application with a required shell command name and
     * optional arguments that are not prefixed with option syntax.
     *
     * To add options, override
     * {@link #addCustomCliOptions(org.apache.commons.cli.Options)}.
     * 
     * @param shellCommand a {@link String}, cannot be <code>null</code> or
     * empty.
     * @param arguments a {@link Argument[]}.
     */
    protected CLI(String shellCommand, Argument[] arguments) {
        if (shellCommand == null || shellCommand.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "shellCommand cannot be null or empty");
        }
        this.shellCommand = shellCommand;

        if (arguments == null) {
            arguments = new Argument[0];
        }
        this.arguments = arguments;
    }

    /**
     * Gets the shell command.
     *
     * @return a {@link String}.
     */
    public final String getShellCommand() {
        return this.shellCommand;
    }

    /**
     * Gets optional arguments that are not prefixed with option syntax.
     *
     * @return a {@link Argument[]}. Guaranteed not <code>null</code>.
     */
    public final Argument[] getArguments() {
        return this.arguments.clone();
    }

    /**
     * Initializes PROTEMPA with the specified configuration. Must be called
     * after {@link #processArgs(java.lang.String[], int, int) }.
     *
     * @param configurationId the id {@link String} of a configuration.
     * @throws ProtempaStartupException if an error occurred starting up
     * PROTEMPA.
     */
    public final void initialize()
            throws ProtempaStartupException {
        this.protempa = Protempa.newInstance(commandLine.getOptionValue("c"));
    }

    /**
     * Implemented with whatever is done with the instance of PROTEMPA
     * created with {@link #initialize(java.lang.String)}, and processing of 
     * any arguments and command line options specified in
     * {@link #addCustomCliOptions(org.apache.commons.cli.Options)}.
     *
     * @param protempa an instance of {@link Protempa}.
     * @param commandLine the {@link CommandLine} options passed in by the
     * caller.
     * @throws CLIException if some exception was thrown by the implementation
     * of this method. The convention is for any such exceptions to be nested
     * in an instance of {@link CLIException}.
     */
    public abstract void execute(Protempa protempa, CommandLine commandLine)
            throws CLIException;

    /**
     * Prints to the console an exception's message and its nested
     * exception's message (if any).
     *
     * @param cliException the {@link Exception} to print.
     */
    public final void printException(Exception cliException) {
        System.err.print(cliException.getMessage());
        Throwable cause = cliException.getCause();
        if (cause != null && cause.getMessage().length() > 0) {
            System.err.print(": ");
            System.err.println(cause.getMessage());
        } else {
            System.err.println();
        }
    }

    /**
     * Calls the code in
     * {@link #execute(org.protempa.Protempa, org.apache.commons.cli.CommandLine)}.
     * @throws CLIException if
     * {@link #execute(org.protempa.Protempa, org.apache.commons.cli.CommandLine)}
     * throws an exception.
     */
    public final void execute() throws CLIException {
        execute(this.protempa, this.commandLine);
    }

    /**
     * Closes the instance of {@link Protempa} created by {@link #initialize(java.lang.String)}.
     */
    public final void close() {
        this.protempa.close();
    }

    /**
     * Processes command line options. The default command line options are -h
     * or --help, which prints usage and the list of options, and -c or
     * --configuration, which is followed by the id of a PROTEMPA
     * configuration. The latter is required unless -h is specified. Any additional
     * options that are specified in {@link #addCustomCliOptions(org.apache.commons.cli.Options)}
     * should be handled by {@link #execute(org.protempa.Protempa, org.apache.commons.cli.CommandLine) }.
     * There always must be at least one argument for the configuration id.
     *
     * @param args the argument {@link String[]} passed into <code>main</code>.
     */
    public final void processOptionsAndArgs(String[] args) {
        if (args == null) {
            throw new IllegalArgumentException("args cannot be null");
        }
        

        Options cliOptions = constructCliOptions();

        CommandLineParser parser = new PosixParser();
        try {
            commandLine = parser.parse(cliOptions, args);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        String commandLineSyntax = commandLineSyntax(arguments);

        if (commandLine.hasOption("h")) {
            new HelpFormatter().printHelp(commandLineSyntax, cliOptions);
            System.exit(0);
        }

        if (!commandLine.hasOption("c")) {
            System.err.println("missing option: c");
            System.exit(1);
        }

        checkInvalidArguments(arguments);

    }

    /**
     * Like calling {@link #initialize(java.lang.String)},
     * {@link #execute()} and {@link #close()} in succession. Must be called
     * only after {@link #processArgs(java.lang.String[], int, int) }.
     *
     * @param configurationId a PROTEMPA configuration id {@link String}.
     */
    public final void initializeExecuteAndClose() {
        try {
            initialize();
        } catch (ProtempaStartupException ex) {
            printException(ex);
            System.exit(1);
        }
        boolean error = false;
        try {
            execute();
        } catch (CLIException ex) {
            printException(ex);
            error = true;
        } finally {
            close();
        }
        if (error) {
            System.exit(1);
        }
    }

    /**
     * Override to specify any custom command line options.
     *
     * @param options the command line {@link Options}.
     */
    protected void addCustomCliOptions(Options options) {
    }

    private String commandLineSyntax(Argument[] arguments) {
        StringBuilder argumentStringBuilder = new StringBuilder();
        if (arguments.length > 0) {
            argumentStringBuilder.append(' ');
        }
        for (int i = 0; i < arguments.length; i++) {
            argumentStringBuilder.append(arguments[i].getFormatted());
            if (i < arguments.length - 1) {
                argumentStringBuilder.append(' ');
            }
        }
        String commandLineSyntax = this.shellCommand + " [options]"
                + argumentStringBuilder.toString();
        return commandLineSyntax;
    }

    private Options constructCliOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("c", "configuration", true,
                "PROTEMPA configuration id");
        addCustomCliOptions(options);
        return options;
    }

    private void checkInvalidArguments(Argument[] arguments) {
        String[] leftOverArgs = commandLine.getArgs();
        List<Argument> requiredArguments = new ArrayList<Argument>();
        for (Argument argument : arguments) {
            if (argument.isRequired()) {
                requiredArguments.add(argument);
            }
        }

        List<String> missingRequiredArgs = new ArrayList<String>();
        if (leftOverArgs.length < requiredArguments.size()) {
            for (int i = leftOverArgs.length, n = requiredArguments.size();
                    i < n; i++) {
                missingRequiredArgs.add(requiredArguments.get(i).getName());
            }
        }

        if (!missingRequiredArgs.isEmpty()) {
            System.err.println("Missing argument(s): "
                    + StringUtils.join(missingRequiredArgs, ", "));
            System.exit(1);
        }

        if (leftOverArgs.length > arguments.length) {
            List<String> extraArgs = new ArrayList<String>();
            for (int i = arguments.length; i < leftOverArgs.length; i++) {
                extraArgs.add(leftOverArgs[i]);
            }
            System.err.println("Invalid extra argument(s): " +
                    StringUtils.join(extraArgs, ","));
            System.exit(1);
        }
        
    }
}
