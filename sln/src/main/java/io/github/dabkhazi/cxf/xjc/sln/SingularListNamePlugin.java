// Copyright 2026 Dmitry Abkhazi
// SPDX-License-Identifier: Apache-2.0
package io.github.dabkhazi.cxf.xjc.sln;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.ErrorHandler;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

public class SingularListNamePlugin {

    private static final System.Logger LOGGER = System.getLogger(SingularListNamePlugin.class.getName());

    public SingularListNamePlugin() {
    }

    public String getOptionName() {
        return "Xsingular-names";
    }

    public String getUsage() {
        return "  -Xsingular-names                 : Reverts plural List field names to their singular @XmlElement name";
    }

    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) {
        LOGGER.log(System.Logger.Level.INFO, "Running singular list name plugin.");
        for (ClassOutline classOutline : outline.getClasses()) {
            JDefinedClass implClass = classOutline.implClass;
            Map<String, String> renameMap = new LinkedHashMap<>();
            Map<String, JFieldVar> fields = implClass.fields();
            for (Map.Entry<String, JFieldVar> entry : fields.entrySet()) {
                JFieldVar field = entry.getValue();
                if (!field.type().fullName().startsWith("java.util.List"))
                    continue;
                JAnnotationUse xmlElement = field.annotations().stream()
                        .filter(a -> a.getAnnotationClass().fullName().equals("jakarta.xml.bind.annotation.XmlElement")
                                ||
                                a.getAnnotationClass().fullName().equals("javax.xml.bind.annotation.XmlElement"))
                        .findFirst()
                        .orElse(null);
                if (xmlElement == null)
                    continue;
                JAnnotationValue nameValue = xmlElement.getAnnotationMembers().get("name");
                if (nameValue == null)
                    continue;
                String singularName = nameValue.toString().replace("\"", "");
                String currentFieldName = entry.getKey();
                if (!currentFieldName.equals(singularName)) {
                    renameMap.put(currentFieldName, Character.toLowerCase(singularName.charAt(0)) + singularName.substring(1));
                }
            }

            for (Map.Entry<String, String> rename : renameMap.entrySet()) {
                String oldName = rename.getKey();
                String newName = rename.getValue();
                JFieldVar field = implClass.fields().get(oldName);
                field.name(newName);
                // Update propOrder in @XmlType
                for (JAnnotationUse annotation : implClass.annotations()) {
                    if (annotation.getAnnotationClass().fullName().equals("jakarta.xml.bind.annotation.XmlType") ||
                        annotation.getAnnotationClass().fullName().equals("javax.xml.bind.annotation.XmlType")) {
                        JAnnotationValue propOrderVal = annotation.getAnnotationMembers().get("propOrder");
                        if (propOrderVal instanceof JAnnotationArrayMember arrayMember) {
                            List<JAnnotationValue> existing = new ArrayList<>(arrayMember.annotations());
                            List<String> updatedNames = new ArrayList<>(arrayMember.annotations().size());
                            for (JAnnotationValue val : existing) {
                                String name = val.toString().replace("\"", "");
                                updatedNames.add(renameMap.getOrDefault(name, name));
                            }
                            JAnnotationArrayMember newArray = annotation.paramArray("propOrder");
                            for (String updated : updatedNames) {
                                newArray.param(updated);
                            }
                        }
                    }
                }
            }
            // Update getter names
            List<JMethod> methods = new ArrayList<>(implClass.methods());
            for (JMethod method : methods) {
                String methodName = method.name();
                if (methodName.startsWith("get")) {
                    String property = decapitalize(methodName.substring(3));
                    String newProperty = renameMap.get(property);
                    if (newProperty != null) {
                        String newName = "get" + capitalize(newProperty);
                        LOGGER.log(System.Logger.Level.INFO, "Changing method name from " + methodName + " to " + newName);
                        method.name(newName);
                        method.javadoc().add(
                                             "\nThis getter has been renamed from " + methodName + "() to "
                                                 + newName + "() by cxf-xjc-singular-names plugin.");
                    }
                }
            }
        }
        return true;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String decapitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

}
