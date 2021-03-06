/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.codehaus.preon.codec;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.lang.reflect.AnnotatedElement;

import junit.framework.TestCase;

import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.LengthPrefix;
import org.codehaus.preon.annotation.Slice;
import org.codehaus.preon.buffer.BitBuffer;


public class SlicingCodecDecoratorTest extends TestCase {

    private BitBuffer buffer;
    private BitBuffer slice;
    private AnnotatedElement metadata;
    private LengthPrefix prefix;
    private Codec decorated;
    private ResolverContext context;
    private Resolver resolver;

    public void setUp() {
        buffer = createMock(BitBuffer.class);
        slice = createMock(BitBuffer.class);
        metadata = createMock(AnnotatedElement.class);
        prefix = createMock(LengthPrefix.class);
        decorated = createMock(Codec.class);
        resolver = createMock(Resolver.class);
        context = createMock(ResolverContext.class);
    }

    public void testSlicingWithSliceAnnotation() throws DecodingException {
        Test2 value = new Test2();

        // Stuff happening when we are decoding
        expect(buffer.slice(8L)).andReturn(slice);
        expect(decorated.decode(slice, resolver, null)).andReturn(value);

        replay(metadata, prefix, decorated, resolver, buffer, slice, context);
        SlicingCodecDecorator factory = new SlicingCodecDecorator();
        Codec<Test2> codec = factory.decorate(decorated, metadata, Test2.class,
                context);
        codec.decode(buffer, resolver, null);

        verify(metadata, prefix, decorated, resolver, buffer, slice, context);
    }

    public void testNoAnnotationsNoNothing() throws DecodingException {
        expect(metadata.isAnnotationPresent(Slice.class)).andReturn(false);

        replay(metadata, prefix, decorated, resolver, buffer, slice, context);
        SlicingCodecDecorator factory = new SlicingCodecDecorator();
        Codec<Test3> codec = factory.decorate(decorated, metadata, Test3.class,
                context);
        assertEquals(codec, decorated);

        verify(metadata, prefix, decorated, resolver, buffer, slice, context);
    }

    @Slice(size = "8")
    public static class Test2 {

    }

    public static class Test3 {

    }

}
