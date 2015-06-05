package org.minimalcode.beans;

import org.junit.Test;
import org.minimalcode.reflect.Property;
import org.minimalcode.reflect.util.GenericBean;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ObjectWrapperSetTest {

    @Test
    public void testSetValue() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        final String value = "new-value";
        wrapper.setValue(GenericBean.STRING_PROPERTY.getName(), value);
        assertEquals(value, genericBean.getStringProperty());
    }

    @Test
    public void testGetValueTraversing() {
        final String value = "value";

        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        wrapper.setValue(
                GenericBean.BEAN_PROPERTY.getName() + "." +
                GenericBean.BEAN_PROPERTY.getName() + "." +
                GenericBean.ARRAY_PROPERTY.getName() + "[0]", value);

        assertEquals(value, genericBean.getBeanProperty().getBeanProperty().getArrayProperty()[0]);
    }

    @Test
    public void testSetValueWithInvalidPattern() {
        try {
            new ObjectWrapper(new GenericBean())
                    .setValue(GenericBean.ARRAY_PROPERTY.getName() + "[text]", null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("Cannot parse"));
        }
    }

    @Test
    public void testSetValueWithIndexed() {
        GenericBean genericBean = new GenericBean();
        genericBean.setArrayProperty(new String[1]);
        genericBean.setListProperty(new ArrayList<String>());

        final int index = 0;
        final String value = "new-value";
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        wrapper.setValue(GenericBean.LIST_PROPERTY.getName() + "[" + index + "]", value);
        assertEquals(value, genericBean.getListProperty().get(index));

        wrapper.setValue(GenericBean.ARRAY_PROPERTY.getName() + "[" + index + "]", value);
        assertEquals(value, genericBean.getArrayProperty()[index]);
    }

    @Test
    public void testSetValueWithMapped() {
        GenericBean genericBean = new GenericBean();
        genericBean.setMapProperty(new HashMap<String, String>());

        final String key = "key";
        final String value = "new-value";
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        wrapper.setValue(GenericBean.MAP_PROPERTY.getName() + "[" + key + "]", value);
        assertEquals(value, genericBean.getMapProperty().get(key));
    }

    @Test
    public void testSetValueWithMappedWithDotsInsideKey() {
        GenericBean genericBean = new GenericBean();
        genericBean.setMapProperty(new HashMap<String, String>());

        final String key = "key.with.dots";
        final String value = "new-value";
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        wrapper.setValue(GenericBean.MAP_PROPERTY.getName() + "[" + key + "]", value);
        assertEquals(value, genericBean.getMapProperty().get(key));
    }

    @Test
    public void testSetValueWithNull() {
        try {
            new ObjectWrapper(this).setValue(null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testSetSimpleValue() {
        GenericBean genericBean = new GenericBean();

        final String value = "new-value";
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        wrapper.setSimpleValue(GenericBean.STRING_PROPERTY.getName(), value);
        assertEquals(value, genericBean.getStringProperty());
    }

    @Test
    public void testSetSimpleValueInvalidProperty() {
        try {
            new ObjectWrapper(this).setSimpleValue("not-a-property", 1);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("not-a-property"));
        }
    }

    @Test
    public void testSetSimpleValueWithNull() {
        try {
            new ObjectWrapper(this).setSimpleValue((String) null, null);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).setSimpleValue((Property) null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testSetIndexedValueWithListAndArray() {
        GenericBean genericBean = new GenericBean();
        genericBean.setListProperty(new ArrayList<String>());
        genericBean.getListProperty().add(null);
        genericBean.setArrayProperty(new String[1]);

        final int index = 0;
        final String value = "new-value";

        // With String as property
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoGrowing(false);

        wrapper.setIndexedValue(GenericBean.LIST_PROPERTY.getName(), index, value);
        wrapper.setIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), index, value);
        assertEquals(value, genericBean.getListProperty().get(index));
        assertEquals(value, genericBean.getArrayProperty()[index]);

        // With Property
        genericBean.getListProperty().clear();
        genericBean.setArrayProperty(new String[1]);
        genericBean.getListProperty().add(null);
        wrapper.setIndexedValue(GenericBean.LIST_PROPERTY, index, value);
        wrapper.setIndexedValue(GenericBean.ARRAY_PROPERTY, index, value);
        assertEquals(value, genericBean.getListProperty().get(index));
        assertEquals(value, genericBean.getArrayProperty()[index]);
    }

    @Test
    public void testSetIndexedValueWithUnsupportedType() {
        GenericBean genericBean = new GenericBean();
        genericBean.setStringProperty("not-indexed");
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        try {
            wrapper.setIndexedValue(GenericBean.STRING_PROPERTY.getName(), 1, "bar");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.STRING_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("Only List and array"));
        }
    }

    @Test
    public void testSetIndexedValueWUnbound() {
        GenericBean genericBean = new GenericBean();
        genericBean.setListProperty(new ArrayList<String>());
        genericBean.setArrayProperty(new String[0]);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoGrowing(false);

        try {
            wrapper.setIndexedValue(GenericBean.LIST_PROPERTY, 1, 1);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("index is unbound"));
        }

        try {
            wrapper.setIndexedValue(GenericBean.ARRAY_PROPERTY, 1, 1);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("index is unbound"));
        }
    }

    @Test
    public void testSetIndexedValueInvalidProperty() {
        try {
            new ObjectWrapper(this).setIndexedValue("not-a-property", 1, 1);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("not-a-property"));
        }
    }

    @Test
    public void testSetIndexedValueWithNull() {
        try {
            new ObjectWrapper(this).setIndexedValue((Property) null, 0, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).setIndexedValue((String) null, 0, null);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).setIndexedValue((Property) null, 0, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testSetMappedValue() {
        GenericBean genericBean = new GenericBean();
        genericBean.setMapProperty(new HashMap<String, String>());

        final String key = "key";
        final String value = "value";
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        // With String as Property
        wrapper.setMappedValue(GenericBean.MAP_PROPERTY.getName(), key, value);
        assertEquals(value, genericBean.getMapProperty().get(key));

        // With Property
        genericBean.getMapProperty().clear();
        wrapper.setMappedValue(GenericBean.MAP_PROPERTY, key, value);
        assertEquals(value, genericBean.getMapProperty().get(key));
    }


    @Test
    public void testSetMappedValueInvalidProperty() {
        try {
            new ObjectWrapper(this).setMappedValue("not-a-property", 1, 1);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("not-a-property"));
        }
    }

    @Test
    public void testSetMappedValueWithNotMapped() {
        GenericBean genericBean = new GenericBean();
        genericBean.setStringProperty("not-a-mapped-property");

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);

        try {
            wrapper.setMappedValue(GenericBean.STRING_PROPERTY, "key1", "value1");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.STRING_PROPERTY.getName()));
        }
    }

    @Test
    public void testSetMappedValueWithNull() {
        try {
            new ObjectWrapper(this).setMappedValue((String) null, null, null);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).setMappedValue((Property) null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }
}