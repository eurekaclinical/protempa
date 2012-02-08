/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.io;

import java.sql.SQLException;
import junit.framework.TestCase;

/**
 *
 * @author Andrew Post
 */
public class RetryerTest extends TestCase {
    public static class MockRetryable implements Retryable<SQLException> {
        private final int failUntilAttempt;
        private int attempt;

        MockRetryable(int failUntilAttempt) {
            this.failUntilAttempt = failUntilAttempt;
        }

        @Override
        public SQLException attempt() {
            this.attempt++;
            if (this.attempt < this.failUntilAttempt) {
                return new SQLException("Attempt failed!");
            } else {
                return null;
            }
        }

        @Override
        public void recover() {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    public void testSuccessfulOnFirstTry() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(1);
        retryer.execute(operation);
        assertTrue(retryer.getErrors().isEmpty());
    }
    
    public void testSuccessfulOnFirstTryNumberOfAttempts() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(1);
        retryer.execute(operation);
        assertEquals(1, retryer.getAttempts());
    }
    
    public void testSuccessfulOnSecondTry() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(2);
        retryer.execute(operation);
        assertEquals(1, retryer.getErrors().size());
    }
    
    public void testSuccessfulOnSecondTryNumberOfAttempts() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(2);
        retryer.execute(operation);
        assertEquals(2, retryer.getAttempts());
    }
    
    public void testSuccessfulOnThirdTry() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(3);
        retryer.execute(operation);
        assertEquals(2, retryer.getErrors().size());
    }
    
    public void testSuccessfulOnThirdTryNumberOfAttempts() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(3);
        retryer.execute(operation);
        assertEquals(3, retryer.getAttempts());
    }
    
    public void testSuccessful() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(4);
        assertTrue(retryer.execute(operation));
    }
    
    public void testSuccessfulNumberOfAttempts() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(4);
        retryer.execute(operation);
        assertEquals(4, retryer.getAttempts());
    }
    
    public void testSuccessfulOnFourthTryCheckErrors() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(4);
        retryer.execute(operation);
        assertEquals(3, retryer.getErrors().size());
    }
    
    public void testNeverSuccessful() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(5);
        assertFalse(retryer.execute(operation));
    }
    
    public void testNeverSuccessfulNumberOfAttempts() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(5);
        retryer.execute(operation);
        assertEquals(4, retryer.getAttempts());
    }
    
    public void testFailedAfterFourAttempts() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(3);
        MockRetryable operation = new MockRetryable(5);
        retryer.execute(operation);
        assertEquals(4, retryer.getErrors().size());
    }
    
    public void testFailOnInvalidNumberOfRetries() {
        try {
            new Retryer<SQLException>(-1);
            fail();
        } catch (IllegalArgumentException iae) {
            
        }
    }
    
    public void testNoRetriesFail() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(0);
        MockRetryable operation = new MockRetryable(4);
        retryer.execute(operation);
        assertEquals(1, retryer.getErrors().size());
    }
    
    public void testNoRetriesFailNumberOfAttempts() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(0);
        MockRetryable operation = new MockRetryable(4);
        retryer.execute(operation);
        assertEquals(1, retryer.getAttempts());
    }
    
    public void testNoRetriesSuccess() {
        Retryer<SQLException> retryer = new Retryer<SQLException>(0);
        MockRetryable operation = new MockRetryable(1);
        assertTrue(retryer.execute(operation));
    }
}
