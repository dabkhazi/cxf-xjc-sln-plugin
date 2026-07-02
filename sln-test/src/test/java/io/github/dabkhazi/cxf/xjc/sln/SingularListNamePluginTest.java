// Copyright 2026 Dmitry Abkhazi
// SPDX-License-Identifier: Apache-2.0
package io.github.dabkhazi.cxf.xjc.sln;

import org.apache.cxf.configuration.foo.Foo;

import org.junit.Test;

public class SingularListNamePluginTest {

    @Test
    public void testPluralRemoval() throws Exception {
        Foo foo = new org.apache.cxf.configuration.foo.ObjectFactory().createFoo();
        foo.getPoint();
    }

}

