package org.minimalcode.beans;

import org.junit.Test;
import org.minimalcode.reflect.Property;
import org.minimalcode.reflect.util.GenericBean;

import java.util.*;

import static org.junit.Assert.*;

public class ObjectWrapperGetTest {

    @Test
    public void testGetValue() {
        final String value = "value";

        GenericBean genericBean = new GenericBean();
        genericBean.setStringProperty(value);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(value, wrapper.getValue(GenericBean.STRING_PROPERTY.getName()));
    }

    @Test
    public void testGetValueTraversing() {
        final String value = "value";

        GenericBean genericBean = new GenericBean();
        genericBean.setBeanProperty(new GenericBean());
        genericBean.getBeanProperty().setBeanProperty(new GenericBean());
        genericBean.getBeanProperty().getBeanProperty().setArrayProperty(new String[]{value});

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(value, wrapper.getValue(
                GenericBean.BEAN_PROPERTY.getName() + "." +
                GenericBean.BEAN_PROPERTY.getName() + "." +
                GenericBean.ARRAY_PROPERTY.getName() + "[0]"));
    }

    @Test
    public void testGetValueNested() {
        GenericBean genericBean = new GenericBean();
        GenericBean innerBean = new GenericBean();
        innerBean.setStringProperty("test");
        genericBean.setBeanProperty(innerBean);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(innerBean.getStringProperty(), wrapper.getValue(
                GenericBean.BEAN_PROPERTY.getName() + "." + GenericBean.STRING_PROPERTY.getName()));
    }

    @Test
    public void testGetValueWithIndexedPattern() {
        GenericBean genericBean = new GenericBean();
        genericBean.setArrayProperty(new String[] {"test1", "test2"});

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(genericBean.getArrayProperty()[0], wrapper.getValue(GenericBean.ARRAY_PROPERTY.getName() + "[0]"));
        assertEquals(genericBean.getArrayProperty()[1], wrapper.getValue(GenericBean.ARRAY_PROPERTY.getName() + "[1]"));
    }

    @Test
    public void testGetValueWithIndexedPatternAndNull() {
        try {
            new ObjectWrapper(new GenericBean()).getValue(GenericBean.ARRAY_PROPERTY.getName() + "[0]");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetValueWithInvalidPattern() {
        try {
            new ObjectWrapper(new GenericBean()).getValue(GenericBean.ARRAY_PROPERTY.getName() + "[text]");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("Cannot parse"));
        }
    }

    @Test
    public void testGetValueWithMappedPattern() {
        final String key = "key";
        final String value = "value";

        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);

        GenericBean genericBean = new GenericBean();
        genericBean.setMapProperty(map);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(value, wrapper.getValue(GenericBean.MAP_PROPERTY.getName() + "[" + key + "]"));
    }

    @Test
    public void testGetValueWithMappedPatternWithDotsInsideKey() {
        final String key = "key.with.dots";
        final String value = "value";

        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);

        GenericBean genericBean = new GenericBean();
        genericBean.setMapProperty(map);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(value, wrapper.getValue(GenericBean.MAP_PROPERTY.getName() + "[" + key + "]"));
    }

    @Test
    public void testGetValueWithNull() {
        try {
            new ObjectWrapper(this).getValue(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetValueWithTraversingNull() {
        try {
            GenericBean genericBean = new GenericBean();
            new ObjectWrapper(genericBean).getValue(GenericBean.BEAN_PROPERTY.getName() + ".not-a-property");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("not-a-property"));
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetSimpleValue() {
        GenericBean genericBean = new GenericBean();
        genericBean.setStringProperty("test");

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(genericBean.getStringProperty(), wrapper.getSimpleValue(GenericBean.STRING_PROPERTY));
        assertEquals(genericBean.getStringProperty(), wrapper.getSimpleValue(GenericBean.STRING_PROPERTY.getName()));
    }

    @Test
    public void testGetSimpleValueInvalidProperty() {
        try {
            new ObjectWrapper(this).getSimpleValue("not-a-property");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("not-a-property"));
        }
    }

    @Test
    public void testGetSimpleValueWithNull() {
        try {
            new ObjectWrapper(this).getSimpleValue((String) null);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).getSimpleValue((Property) null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetIndexedValueInvalidProperty() {
        try {
            new ObjectWrapper(this).getIndexedValue("not-a-property", 1);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("not-a-property"));
        }
    }

    @Test
    public void testGetIndexedValueWithNotIndexed() {
        try {
            GenericBean genericBean = new GenericBean();
            genericBean.setStringProperty("not-indexed");

            new ObjectWrapper(genericBean).getIndexedValue(GenericBean.STRING_PROPERTY.getName(), 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.STRING_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("not indexed"));
        }
    }

    @Test
    public void testGetIndexValueWithNull() {
        try {
            new ObjectWrapper(this).getIndexedValue((String) null, 0);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).getIndexedValue((Property) null, 0);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetIndexedValueWithArray() {
        GenericBean genericBean = new GenericBean();
        genericBean.setArrayProperty(new String[]{"test1", "test2"});

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(genericBean.getArrayProperty()[0], wrapper.getIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), 0));
        assertEquals(genericBean.getArrayProperty()[1], wrapper.getIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), 1));

        assertEquals(genericBean.getArrayProperty()[0], wrapper.getIndexedValue(GenericBean.ARRAY_PROPERTY, 0));
        assertEquals(genericBean.getArrayProperty()[1], wrapper.getIndexedValue(GenericBean.ARRAY_PROPERTY, 1));
    }

    @Test
    public void testGetIndexedValueWithList() {
        List<String> list = new ArrayList<String>();
        list.add("test1");

        GenericBean genericBean = new GenericBean();
        genericBean.setListProperty(list);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(genericBean.getListProperty().get(0), wrapper.getIndexedValue(GenericBean.LIST_PROPERTY.getName(), 0));
        assertEquals(genericBean.getListProperty().get(0), wrapper.getIndexedValue(GenericBean.LIST_PROPERTY, 0));
    }

    @Test
    public void testGetIndexedValueWithIterable() {
        final String value = "new-value";
        final String value1 = "new-value2";

        Set<String> set = new LinkedHashSet<String>();
        set.add(value);
        set.add(value1);// must be two, for testing iteration++

        GenericBean genericBean = new GenericBean();
        genericBean.setIterableProperty(set);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(value, wrapper.getIndexedValue(GenericBean.ITERABLE_PROPERTY.getName(), 0));
        assertEquals(value1, wrapper.getIndexedValue(GenericBean.ITERABLE_PROPERTY.getName(), 1));
        assertEquals(value, wrapper.getIndexedValue(GenericBean.ITERABLE_PROPERTY, 0));
        assertEquals(value1, wrapper.getIndexedValue(GenericBean.ITERABLE_PROPERTY, 1));
    }

    @Test
    public void testGetIndexedValueWithUnclosedPattern() {
        try {
            new ObjectWrapper(new GenericBean()).getValue(GenericBean.LIST_PROPERTY.getName() + "[1");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("Cannot found the closing ']'"));
        }
    }

    @Test
    public void testGetMappedValue() {
        final String key = "key";
        final String value = "value";

        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);

        GenericBean genericBean = new GenericBean();
        genericBean.setMapProperty(map);

        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        assertEquals(genericBean.getMapProperty().get(key),
                wrapper.getMappedValue(GenericBean.MAP_PROPERTY.getName(), key));

        assertEquals(genericBean.getMapProperty().get(key),
                wrapper.getMappedValue(GenericBean.MAP_PROPERTY, key));
    }

    @Test
    public void testGetMappedValueNotPresentProperty() {
        try {
            new ObjectWrapper(this).getMappedValue("not-a-property", 1);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("not-a-property"));
        }
    }

    @Test
    public void testGetMappedValueWithNull() {
        try {
            new ObjectWrapper(new GenericBean()).getMappedValue(GenericBean.MAP_PROPERTY.getName(), "key1");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).getMappedValue((String) null, "key1");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null"));
        }

        try {
            new ObjectWrapper(this).getMappedValue((Property) null, "key1");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testGetMappedValueWithNotMapped() {
        try {
            GenericBean genericBean = new GenericBean();
            genericBean.setStringProperty("not-mapped");

            new ObjectWrapper(genericBean).getMappedValue(GenericBean.STRING_PROPERTY.getName(), "test");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.STRING_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("not mapped"));
        }
    }
}