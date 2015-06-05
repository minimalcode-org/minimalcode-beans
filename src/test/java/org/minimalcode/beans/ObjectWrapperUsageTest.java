package org.minimalcode.beans;

import org.junit.Assert;
import org.junit.Test;
import org.minimalcode.reflect.Property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ObjectWrapperUsageTest {

    @Test
    public void testRealisticUsage() {
        MyBean myBean = new MyBean();
        myBean.setName("foo");
        processUpperCaseAnnotation(myBean);

        Assert.assertEquals("FOO", myBean.getName());
    }

    private void processUpperCaseAnnotation(Object object) {
        ObjectWrapper wrapper = new ObjectWrapper(object);

        for (Property property : wrapper.getBean().getProperties()) {
            if (!property.isAnnotationPresent(UpperCase.class)) {
                continue;
            }

            if(property.getType() != String.class) {
                throw new IllegalArgumentException("@UpperCase must annote a String firstProperty.");
            }

            if(property.isReadable()) {
                Object value = wrapper.getSimpleValue(property);

                if (property.isWritable() && value != null) {
                    wrapper.setSimpleValue(property, ((String) value).toUpperCase());
                }
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface UpperCase {}

    public static class MyBean {
        @UpperCase
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
