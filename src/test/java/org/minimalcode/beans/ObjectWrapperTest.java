package org.minimalcode.beans;

import org.junit.Test;
import org.minimalcode.reflect.Bean;
import org.minimalcode.reflect.util.GenericBean;

import static org.junit.Assert.*;

public class ObjectWrapperTest {

    @Test
    public void testWrap() {
        ObjectWrapper wrapper = new ObjectWrapper(this);
        wrapper.setWrappedObject(new GenericBean());

        assertEquals(wrapper.getBean(), Bean.forClass(GenericBean.class));
    }

    @Test
    public void testWrapWithNull() {
        try {
            new ObjectWrapper(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetBean() {
        ObjectWrapper wrapper = new ObjectWrapper(new GenericBean());
        assertEquals(wrapper.getBean(), Bean.forClass(GenericBean.class));
    }

    @Test
    public void testGetProperty() {
        ObjectWrapper wrapper = new ObjectWrapper(new GenericBean());
        assertNull(wrapper.getProperty("not-a-property"));
        assertNull(wrapper.getProperty("not.a.property.nested.with.dots"));
        assertNotNull(wrapper.getProperty(GenericBean.STRING_PROPERTY.getName()));
        assertNotNull(wrapper.getProperty(GenericBean.BEAN_PROPERTY.getName()
                + "." + GenericBean.STRING_PROPERTY.getName()));
    }

    @Test
    public void testGetPropertyWithNull() {
        try {
            new ObjectWrapper(this).getProperty(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetPropertyWithSquares() {
        try {
            new ObjectWrapper(new GenericBean()).getProperty(GenericBean.STRING_PROPERTY.getName() + "[1]");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.STRING_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("'[]' notation is not allowed"));
        }
    }

    @Test
    public void testToString() throws Exception {
        GenericBean genericBean = new GenericBean();
        assertEquals("ObjectWrapper{object=" + genericBean + '}', new ObjectWrapper(genericBean).toString());
    }
}
