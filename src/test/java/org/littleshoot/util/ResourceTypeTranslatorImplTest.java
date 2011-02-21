package org.littleshoot.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for resource type translation.
 */
public class ResourceTypeTranslatorImplTest
    {

    @Test public void testTranslations() throws Exception
        {
        final ResourceTypeTranslator translator = 
            new ResourceTypeTranslatorImpl();
        
        
        String type = translator.getType("file.ogg");
        assertEquals("audio", type);
        }
    }
