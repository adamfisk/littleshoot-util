package org.littleshoot.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IoUtilsTest
    {

    @Test public void test() throws Exception
        {
        final String input = 
            "JOEIJSIOJREOIFJNEOFJISJ:F:SJFSKLJFLK:SJFKLSiFPuep9q57894375pqfj"+
            "JOEIJSIOJREOIFJNEOFJISJ:F:SJFSKLJFLK:SJFKLSiFPuep9q57894375pqfj"+
            "JOEIJSIOJREOIFJNEOFJISJ:F:SJFSKLJFLK:SJFKLSiFPuep9q57894375pqfj"+
            "JOEIJSIOJREOIFJNEOFJISJ:F:SJFSKLJFLK:SJFKLSiFPuep9q57894375pqfj"+
            "JOEIJSIOJREOIFJNEOFJISJ:F:SJFSKLJFLK:SJFKLSiFPuep9q57894375pqfj"+
            "JOEIJSIOJREOIFJNEOFJISJ:F:SJFSKLJFLK:SJFKLSiFPuep9q57894375pqfj";
        final byte[] deflated = IoUtils.deflate(input);
        
        //System.out.println(deflated);
        final int inSize = input.length();
        final int outSize = deflated.length;
        assertTrue(inSize > outSize);
        
        final byte[] inflated = IoUtils.inflate(deflated);
        
        final String inflatedString = new String(inflated, "UTF-8");
        assertEquals(input, inflatedString);
        
        assertEquals(input, IoUtils.inflateString(deflated));
        }
    }
