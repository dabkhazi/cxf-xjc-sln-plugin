// Copyright 2026 Dmitry Abkhazi
// SPDX-License-Identifier: Apache-2.0
package com.sun.tools.xjc.addon.apache_cxf.sln;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.Outline;

public class SingularListNamePlugin extends Plugin {

    /*
     * XJC discovers Plugin implementations through META-INF/services.
     * Keep this adapter while the implementation class does not extend Plugin.
     */
    private final io.github.dabkhazi.cxf.xjc.sln.SingularListNamePlugin impl =
            new io.github.dabkhazi.cxf.xjc.sln.SingularListNamePlugin();

    @Override
    public String getOptionName() {
        return impl.getOptionName();
    }

    @Override
    public String getUsage() {
        return impl.getUsage();
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        return impl.run(outline, opt, errorHandler);
    }

}
