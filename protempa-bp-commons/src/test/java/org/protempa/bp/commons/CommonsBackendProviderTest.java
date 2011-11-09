package org.protempa.bp.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.BackendProvider;
import org.protempa.backend.BackendProviderManager;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.BackendSpecLoader;
import org.protempa.backend.BackendSpecNotFoundException;
import org.protempa.bp.commons.asb.JavaAlgorithmBackend;
import org.protempa.bp.commons.ksb.PropertiesFileKnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class CommonsBackendProviderTest {

    private static BackendProvider backendProvider;

    @BeforeClass
    public static void initBackendProvider() {
        backendProvider = BackendProviderManager.getBackendProvider();
    }

    @BeforeClass
    public static void initBackends() {
        System.setProperty(DataSourceBackend.class.getName(),
                MockDataSourceBackend.class.getName());
        System.setProperty(KnowledgeSourceBackend.class.getName(),
                MockKnowledgeSourceBackend.class.getName());
        System.setProperty(AlgorithmSourceBackend.class.getName(),
                MockAlgorithmSourceBackend.class.getName());
    }

    @AfterClass
    public static void shutdownBackendProvider() {
        backendProvider = null;
    }

    @Test
    public void testLoadServiceLoaderBackendProvider() {
        assertEquals(CommonsBackendProvider.class,
                backendProvider.getClass());
    }

    /**
     * Test of getDisplayName method, of class CommonsBackendProvider.
     */
    @Test
    public void testGetDisplayName() {
        assertEquals(CommonsUtil.resourceBundle().getString(
                "displayName"),
                backendProvider.getDisplayName());
    }

    @Test
    public void testEnumerateDataSourceBackends() 
            throws BackendProviderSpecLoaderException {
        Set<String> backendIds = new HashSet<String>();
        for (BackendSpec<?> spec :
            backendProvider.getDataSourceBackendSpecLoader()) {
            backendIds.add(spec.getId());
        }
        assertEquals(
                Collections.singleton(MockDataSourceBackend.class.getName()),
                backendIds);
    }

    /**
     * Test of getDataSourceBackendLoader method, of class CommonsBackendProvider.
     */
    @Test
    public void testGetDataSourceBackendLoader() 
            throws BackendProviderSpecLoaderException {
        BackendSpecLoader<?> result = 
                backendProvider.getDataSourceBackendSpecLoader();
        assertNotNull(result);
    }

    @Test
    public void testGetDataSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> result =
                backendProvider.getDataSourceBackendSpecLoader();
        result.loadSpec(MockDataSourceBackend.class.getName());
    }

    @Test
    public void testGetDataSourceBackendInfoDisplayName()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getDataSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockDataSourceBackend.class.getName());
        assertEquals("Mock Data Source Backend", spec.getDisplayName());
    }

    @Test
    public void testGetDataSourceBackendInfoId()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getDataSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockDataSourceBackend.class.getName());
        assertEquals(MockDataSourceBackend.class.getName(), spec.getId());
    }

    @Test
    public void testGetDataSourceBackendPropertySpecs()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getDataSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockDataSourceBackend.class.getName());
        BackendInstanceSpec<?> iSpec = spec.newBackendInstanceSpec();
        List<BackendPropertySpec> bps = iSpec.getBackendPropertySpecs();
        assertEquals(1, bps.size());
    }

    /**
     * Test of getKnowledgeSourceBackendLoader method, of class CommonsBackendProvider.
     */
    @Test
    public void testGetKnowledgeSourceBackendLoader() 
            throws BackendProviderSpecLoaderException {
        BackendSpecLoader<?> result = 
                backendProvider.getKnowledgeSourceBackendSpecLoader();
        assertNotNull(result);
    }

    @Test
    public void testEnumerateKnowledgeSourceBackends() 
            throws BackendProviderSpecLoaderException {
        Set<String> backendIds = new HashSet<String>();
        for (BackendSpec<?> spec :
            backendProvider.getKnowledgeSourceBackendSpecLoader()) {
            backendIds.add(spec.getId());
        }
        assertEquals(new HashSet<String>(Arrays.asList(new String[] {
                MockKnowledgeSourceBackend.class.getName(),
                PropertiesFileKnowledgeSourceBackend.class.getName()})),
                backendIds);
    }

    @Test
    public void testGetKnowledgeSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> result =
                backendProvider.getKnowledgeSourceBackendSpecLoader();
        result.loadSpec(MockKnowledgeSourceBackend.class.getName());
    }

    @Test
    public void testGetKnowledgeSourceBackendInfoDisplayName()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getKnowledgeSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockKnowledgeSourceBackend.class.getName());
        assertEquals("Mock Knowledge Source Backend", spec.getDisplayName());
    }

    @Test
    public void testGetKnowledgeSourceBackendInfoId()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getKnowledgeSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockKnowledgeSourceBackend.class.getName());
        assertEquals(MockKnowledgeSourceBackend.class.getName(), spec.getId());
    }

    @Test
    public void testGetKnowledgeSourceBackendPropertySpecs()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getKnowledgeSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockKnowledgeSourceBackend.class.getName());
        BackendInstanceSpec<?> iSpec = spec.newBackendInstanceSpec();
        List<BackendPropertySpec> bps = iSpec.getBackendPropertySpecs();
        assertEquals(1, bps.size());
    }

    @Test
    public void testEnumerateAlgorithmSourceBackends() 
            throws BackendProviderSpecLoaderException {
        Set<String> backendIds = new HashSet<String>();
        for (BackendSpec<?> spec :
            backendProvider.getAlgorithmSourceBackendSpecLoader()) {
            backendIds.add(spec.getId());
        }
        assertEquals(new HashSet<String>(Arrays.asList(new String[] {
                MockAlgorithmSourceBackend.class.getName(),
                JavaAlgorithmBackend.class.getName()})),
                backendIds);
    }

    /**
     * Test of getAlgorithmSourceBackendLoader method, of class CommonsBackendProvider.
     */
    @Test
    public void testGetAlgorithmSourceBackendSpecLoader() 
            throws BackendProviderSpecLoaderException {
        BackendSpecLoader<?> result =
                backendProvider.getAlgorithmSourceBackendSpecLoader();
        assertNotNull(result);
    }

    @Test
    public void testGetAlgorithmSourceBackendSpec()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> result =
                backendProvider.getAlgorithmSourceBackendSpecLoader();
        result.loadSpec(MockAlgorithmSourceBackend.class.getName());
    }

    @Test
    public void testGetAlgorithmSourceBackendInfoDisplayName()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getAlgorithmSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockAlgorithmSourceBackend.class.getName());
        assertEquals("Mock Algorithm Source Backend", spec.getDisplayName());
    }

    @Test
    public void testGetAlgorithmSourceBackendInfoId()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getAlgorithmSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockAlgorithmSourceBackend.class.getName());
        assertEquals(MockAlgorithmSourceBackend.class.getName(), spec.getId());
    }

    @Test
    public void testGetAlgorithmSourceBackendPropertySpecs()
            throws BackendSpecNotFoundException,
            BackendProviderSpecLoaderException {
        BackendSpecLoader<?> loader =
                backendProvider.getAlgorithmSourceBackendSpecLoader();
        BackendSpec<?> spec = 
                loader.loadSpec(MockAlgorithmSourceBackend.class.getName());
        BackendInstanceSpec<?> iSpec = spec.newBackendInstanceSpec();
        List<BackendPropertySpec> bps = iSpec.getBackendPropertySpecs();
        assertEquals(0, bps.size());
    }

}
