package org.minimalcode.beans;

import org.junit.Test;
import org.minimalcode.reflect.ReflectionException;
import org.minimalcode.reflect.util.GenericBean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ObjectWrapperOptionTest {

    @Test
    public void testSetOption() throws NoSuchFieldException, IllegalAccessException {
        ObjectWrapper wrapper = new ObjectWrapper(this);

        // Autogrowing
        Field autoGrowing = wrapper.getClass().getDeclaredField("isAutoGrowing");
        autoGrowing.setAccessible(true);

        wrapper.setAutoGrowing(false);
        assertEquals(false, autoGrowing.get(wrapper));
        wrapper.setAutoGrowing(true);
        assertEquals(true, autoGrowing.get(wrapper));

        // Autoinstancing
        Field autoInstancing = wrapper.getClass().getDeclaredField("isAutoInstancing");
        autoInstancing.setAccessible(true);

        wrapper.setAutoInstancing(false);
        assertEquals(false, autoInstancing.get(wrapper));
        wrapper.setAutoInstancing(true);
        assertEquals(true, autoInstancing.get(wrapper));

        // OutOfBounds Safety
        Field unboundSafety = wrapper.getClass().getDeclaredField("isOutOfBoundsSafety");
        unboundSafety.setAccessible(true);

        wrapper.setOutOfBoundsSafety(false);
        assertEquals(false, unboundSafety.get(wrapper));
        wrapper.setOutOfBoundsSafety(true);
        assertEquals(true, unboundSafety.get(wrapper));
    }

    @Test
    public void testAutoInstancingWithBean() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(true);

        final String value = "foo";
        wrapper.setValue(GenericBean.BEAN_PROPERTY.getName() + "." + GenericBean.STRING_PROPERTY.getName(), value);
        assertNotNull(genericBean.getBeanProperty());
        assertEquals(value, genericBean.getBeanProperty().getStringProperty());
    }

    @Test
    public void testAutoInstancingWithBeanNotInstantiable() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(true);

        try {
            // listProperty is null and not a bean
            wrapper.setValue(GenericBean.LIST_PROPERTY.getName() + ".foo", "foo");
            fail();
        } catch (ReflectionException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }
    }

    @Test
    public void testAutoInstancingDisabledWithBean() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(false);

        try {
            wrapper.setValue(GenericBean.BEAN_PROPERTY.getName() + "." + GenericBean.STRING_PROPERTY.getName(), "foo");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.STRING_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testAutoInstancingWithListAndArray() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(true);

        final int index = 10;
        final String value = "foo";

        // SetValue
        wrapper.setValue(GenericBean.LIST_PROPERTY.getName() + "[" + index + "]", value);
        wrapper.setValue(GenericBean.ARRAY_PROPERTY.getName() + "[" + index + "]", value);
        assertNotNull(genericBean.getListProperty());
        assertNotNull(genericBean.getArrayProperty());
        assertEquals(value, genericBean.getListProperty().get(index));
        assertEquals(value, genericBean.getArrayProperty()[index]);

        // SetIndexedValue
        genericBean.setListProperty(null);
        genericBean.setArrayProperty(null);

        wrapper.setIndexedValue(GenericBean.LIST_PROPERTY.getName(), index, value);
        wrapper.setIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), index, value);
        assertNotNull(genericBean.getListProperty());
        assertNotNull(genericBean.getArrayProperty());
        assertEquals(value, genericBean.getListProperty().get(index));
        assertEquals(value, genericBean.getArrayProperty()[index]);
    }

    @Test
    public void testAutoInstancingWithIndexedUnsupportedType() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(true);

        try {
            wrapper.setValue(GenericBean.STRING_PROPERTY.getName() + "[1]", "bar");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(GenericBean.STRING_PROPERTY.getName()));
            assertTrue(e.getMessage().contains("Only List and array"));
        }
    }

    @Test
    public void testAutoInstancingDisabledWithListAndArray() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(false);

        // Set Value List
        try {
            wrapper.setValue(GenericBean.LIST_PROPERTY.getName() + "[1]", "bar");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }

        // Set Value Array
        try {
            wrapper.setValue(GenericBean.ARRAY_PROPERTY.getName() + "[1]", "bar");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
        }

        // SetIndexedValue List
        try {
            wrapper.setIndexedValue(GenericBean.LIST_PROPERTY.getName(), 1, "bar");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }

        // SetIndexedValue Array
        try {
            wrapper.setIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), 1, "bar");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
        }
    }

    @Test
    public void testAutoInstancingWithMap() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(true);

        final String key = "bar";
        final String value = "foo";

        // SetValue
        wrapper.setValue(GenericBean.MAP_PROPERTY.getName() + "[" + key + "]", value);
        assertNotNull(genericBean.getMapProperty());
        assertEquals(value, genericBean.getMapProperty().get(key));

        // SetIndexedValue
        genericBean.setMapProperty(null);
        wrapper.setMappedValue(GenericBean.MAP_PROPERTY.getName(), key, value);
        assertNotNull(genericBean.getMapProperty());
        assertEquals(value, genericBean.getMapProperty().get(key));
    }

    @Test
    public void testAutoInstancingDisabledWithMap() {
        GenericBean genericBean = new GenericBean();
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoInstancing(false);

        // SetValue
        try {
            wrapper.setValue(GenericBean.MAP_PROPERTY.getName() + "[foo]", "bar");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.MAP_PROPERTY.getName()));
        }

        // SetMappedValue
        try {
            wrapper.setMappedValue(GenericBean.MAP_PROPERTY.getName(), "foo", "bar");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.MAP_PROPERTY.getName()));
        }
    }

    @Test
    public void testAutoGrowingWithSetValueAndSetIndexedValue() {
        GenericBean genericBean = new GenericBean();
        genericBean.setIterableProperty(new HashSet<String>(0));

        final int index = 10;
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoGrowing(true);

        // List setValue
        genericBean.setListProperty(new ArrayList<String>(0));
        wrapper.setValue(GenericBean.LIST_PROPERTY.getName() + "[" + index + "]", null);
        assertEquals(index + 1, genericBean.getListProperty().size());

        // List setIndexedValue
        genericBean.setListProperty(new ArrayList<String>(0));
        wrapper.setIndexedValue(GenericBean.LIST_PROPERTY.getName(), index, null);
        assertEquals(index + 1, genericBean.getListProperty().size());

        // Array setValue
        genericBean.setArrayProperty(new String[0]);
        wrapper.setValue(GenericBean.ARRAY_PROPERTY.getName() + "[" + index + "]", null);
        assertEquals(index + 1, genericBean.getArrayProperty().length);

        // Array setIndexedValue
        genericBean.setArrayProperty(new String[0]);
        wrapper.setIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), index, null);
        assertEquals(index + 1, genericBean.getArrayProperty().length);
    }

    @Test
    public void testAutoGrowingDisabledWithSetValueAndSetIndexedValue() {
        GenericBean genericBean = new GenericBean();
        genericBean.setListProperty(new ArrayList<String>(0));
        genericBean.setArrayProperty(new String[0]);

        final int index = 10;
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoGrowing(false);

        // List setValue
        try {
            wrapper.setValue(GenericBean.LIST_PROPERTY.getName() + "[" + index + "]", null);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }

        // List setIndexedValue
        try {
            wrapper.setIndexedValue(GenericBean.LIST_PROPERTY.getName(), index, null);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }

        // Array setValue
        try {
            wrapper.setValue(GenericBean.ARRAY_PROPERTY.getName() + "[" + index + "]", null);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
        }

        // Array setIndexedValue
        try {
            wrapper.setIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), index, null);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
        }
    }

    @Test
    public void testAutoGrowingListWithNullNotAllowed() {
        GenericBean genericBean = new GenericBean();
        genericBean.setListProperty(new ArrayList<String>(0) {
            @Override
            public boolean add(String s) {
                if (s == null) {
                    throw new NullPointerException("invalid");
                }

                return super.add(s);
            }
        });

        final int index = 10;
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setAutoGrowing(true);

        // List setValue
        try {
            wrapper.setValue(GenericBean.LIST_PROPERTY.getName() + "[" + index + "]", null);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }

        // List setIndexedValue
        try {
            wrapper.setIndexedValue(GenericBean.LIST_PROPERTY.getName(), index, null);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }
    }

    @Test
    public void testUnboundSafetyWithGetValueAndGetIndexedValue() {
        GenericBean genericBean = new GenericBean();
        genericBean.setListProperty(Collections.<String>emptyList());
        genericBean.setArrayProperty(new String[0]);
        genericBean.setIterableProperty(Collections.<String>emptySet());

        final int index = 10;
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setOutOfBoundsSafety(true);

        // List
        assertNull(wrapper.getValue(GenericBean.LIST_PROPERTY.getName() + "[" + index + "]"));
        assertNull(wrapper.getIndexedValue(GenericBean.LIST_PROPERTY.getName(), index));

        // Array
        assertNull(wrapper.getValue(GenericBean.ARRAY_PROPERTY.getName() + "[" + index + "]"));
        assertNull(wrapper.getIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), index));

        // Iterable
        assertNull(wrapper.getValue(GenericBean.ITERABLE_PROPERTY.getName() + "[" + index + "]"));
        assertNull(wrapper.getIndexedValue(GenericBean.ITERABLE_PROPERTY.getName(), index));
    }

    @Test
    public void testUnboundSafetyDisabledWithGetValueAndGetIndexedValue() {
        GenericBean genericBean = new GenericBean();
        genericBean.setListProperty(Collections.<String>emptyList());
        genericBean.setArrayProperty(new String[0]);
        genericBean.setIterableProperty(Collections.<String>emptySet());

        final int index = 10;
        ObjectWrapper wrapper = new ObjectWrapper(genericBean);
        wrapper.setOutOfBoundsSafety(false);

        // List getValue
        try {
            wrapper.getValue(GenericBean.LIST_PROPERTY.getName() + "[" + index + "]");
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }

        // List getIndexedValue
        try {
            wrapper.getIndexedValue(GenericBean.LIST_PROPERTY.getName(), index);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.LIST_PROPERTY.getName()));
        }

        // Array getValue
        try {
            assertNull(wrapper.getValue(GenericBean.ARRAY_PROPERTY.getName() + "[" + index + "]"));
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
        }

        // Array getIndexedValue
        try {
            assertNull(wrapper.getIndexedValue(GenericBean.ARRAY_PROPERTY.getName(), index));
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.ARRAY_PROPERTY.getName()));
        }

        // Iterable getValue
        try {
            assertNull(wrapper.getValue(GenericBean.ITERABLE_PROPERTY.getName() + "[" + index + "]"));
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.ITERABLE_PROPERTY.getName()));
        }

        // Iterable getIndexedValue
        try {
            assertNull(wrapper.getIndexedValue(GenericBean.ITERABLE_PROPERTY.getName(), index));
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains(GenericBean.ITERABLE_PROPERTY.getName()));
        }
    }
}
