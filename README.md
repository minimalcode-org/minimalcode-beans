# MinimalCode Beans

[![Build Status](https://travis-ci.org/minimalcode-org/minimalcode-beans.svg)](https://travis-ci.org/minimalcode-org/minimalcode-beans)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.minimalcode/minimalcode-beans/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.minimalcode/minimalcode-beans/)
[![Dependency Status](https://www.versioneye.com/user/projects/556dac973934620014010000/badge.svg?style=flat)](https://www.versioneye.com/user/projects/556dac973934620014010000)

MinimalCode Beans, also known as "**Commons BeanUtils for Android**" (as it works everywhere), is a minimalistic Java wrapper built to facilitate generic property getter and setter operations on Java objects. The library is also highly optimized for providing **high-performance** and follows a very strict **low-garbage** policy. 

An elegant source of **only a single class**, robust test suite with **100% coverage**, and **no external dependencies**, make it the natural choice for high-traffic \ low-memory application (like mobile)... or the right tool for all developers who think that there is no need of a nuclear power plant to light up a lamp.

### Installation

The library requires **jdk 1.6+** and the internal [minimalcode-reflect](https://github.com/minimalcode-org/minimalcode-reflect) project (2-classes, auto-imported by maven). 

If [cglib](https://github.com/cglib/cglib) is found in the classpath then the bytecode proxy generation will be automatically enabled to provide better performance, but this dependency is totally **optional** and can be ignored without side effects.

- Maven:

  ```xml
 	<dependencies>
 		<dependency>
        	<groupId>org.minimalcode</groupId>
        	<artifactId>minimalcode-beans</artifactId>
        	<version>0.5.1</version><!-- or last version -->
      	</dependency>
      
      	<!-- Optional Cglib Dependency (avoid it on Android systems) -->
      	<dependency>
        	<groupId>cglib</groupId>
          	<artifactId>cglib</artifactId>
          	<version>3.1</version>
      	</dependency>
  	</dependencies>
  ```
  
### Performance

Following are some performance benchmarks comparing **alternative libraries** with similar functionality.

- [Apache Commons BeanUtils](http://commons.apache.org/proper/commons-beanutils)
- [Azeckoski ReflectUtils](https://github.com/azeckoski/reflectutils)
- [Jodd BeanUtil](https://github.com/oblac/jodd)
- [Spring BeanWrapper](https://github.com/spring-projects/spring-framework)

**Beware, microbenchmarks ahead**! The great variance is due to **many variables**, like the accessibility of specialized indexed\mapped methods, the support of embedded conversion, the number of supported options, or more generally the optimization of the inner implementation. Spring especially supports two different embedded conversion systems (Convert and legacy PropertyEditor), largely penalizing set performance. 

The code of all benchmarks is available in the [minimalcode-beans-benchmark](https://github.com/minimalcode-org/minimalcode-beans-benchmark) project, for public review.

**Get Benchmarks: ns required for each operation (lower is better)**

![Get Benchmark](http://s14.postimg.org/fft212aoh/get_result_new.jpg)

Operation     | Apache    | Azekosky    | MinimalCode   | Jodd      | Spring    |
------------- | -------:  | ---------:  | -----------:  | -------:  | -------:  |
Get Simple    | 238,67    | 181,38      | **18,81**     | 247,03    | 61,31     |
Get Nested    | 791,44    | 495,83      | **135,55**    | 319,31    | 228,86    |
Get Indexed   | 168,06    | 289,93      | **26,69**     | 279,19    | 285,70    |
Get Mapped    | 174,81    | 284,32      | **28,00**     | 329,47    | 880,14    |
Get Total     | 1.372,98  | 1.251,45    | **209,04**    | 1.175,00  | 1.456,01  |

-------------------------------

**Set Benchmarks: ns required for each operation (lower is better)**

![Set Benchmark](http://s14.postimg.org/636926b2p/set_result_new.jpg)

Operation     | Apache    | Azekosky    | MinimalCode   | Jodd      | Spring    |
------------- | -------:  | ---------:  | ----------:   | -------:  | -------:  |
Set Simple    | 252,83    | 377,42      | **22,10**     | 275,18    | 773,88    |
Set Nested    | 815,71    | 706,83      | **143,24**    | 381,56    | 955,95    |
Set Indexed   | 170,56    | 497,18      | **23,27**     | 409,03    | 2.328,39  |
Set Mapped    | 184,83    | 537,32      | **24,89**     | 366,04    | 3.086,70  |
Set Total     | 1.423,93  | 2.118,74    | **213,50**    | 1.431,81  | 7.144,91  |

### ObjectWrapper

**ObjectWrapper** can wrap **any type of object**. In a not-concurrent context, it is also perfectly safe to change the 
wrapped object at any time, without side effects (avoiding instantiating a new wrapper). Options can also be changed at any time.

```java
// New ObjectWrapper
Object obj = new Book();
ObjectWrapper wrapper = new ObjectWrapper(obj);
String bookTitle = (String) wrapper.getSimpleValue("title");// obj.getTitle();

// Switch
Object obj2 = new Author();
wrapper.setWrappedObject(obj2);
int authorAge = (int) wrapper.getSimpleValue("age");// obj2.getAge();
```

### Reflect

**ObjectWrapper** is built upon the **MinimalCode Reflect** framework, which provides raw reflection and properties introspection (like annotations, type etc...). 

It is **highly recommended** (it takes letterally 60 seconds) to read its [README.md](https://github.com/minimalcode-org/minimalcode-reflect#property-accessors), in order to better appreciate all the features available.

The **Bean** of the wrapped object is always available through **ObjectWrapper::getBean**. Note that while each of the following functions also works with a property-name lookup (by String), is always preferible to use the **Property** object directly, when available (for example in loops).

```java
Bean<?> bean = wrapper.getBean();

for(Property property : bean.getProperties()) {
  if(property.isAnnotationPresent(MyAnnotationOne.class)) {
    return wrapper.getSimpleValue(property);
  }
}
```

### Simple Properties

Simple, or scalar, properties have a single value that may be retrieved or modified. The underlying property type might be a  **primitive** (such as int) **or an object** (such as a java.lang.String or java.util.LinkedList).

```java
// Get Simple
String name = (String) wrapper.getSimpleValue("name");// obj.getName();
List<String> authorsList = (List<String>) wrapper.getSimpleValue("authorsList");// obj.getAuthorsList();

// Set Simple
wrapper.setSimpleValue("name", "new-name-value");// obj.setName("new-name-value");
wrapper.setSimpleValue("namesMap", new HashMap<Locale, String>());// obj.setNamesMap(new HashMap<Locale, String>());
```

### Indexed Properties

An indexed property is a **java.util.List** or an **array** type which stores an ordered collection of objects (all of the same type) that can be individually accessed by an integer-valued, non-negative index. As an extension the wrapper considers, for retrieving only, any **finite java.lang.Iterable<T>** (Collection, List, Set...) to be indexed as well.

```java
// Get Indexed (List, Iterable, Collection, array...)
String author = (String) wrapper.getIndexedValue("authorsList", 1);// obj.getAuthorsList().get(1);
Chapter chapter = (Chapter) wrapper.getIndexedValue("chaptersArray", 3);// obj.getChaptersArray()[3];
Tag favorite = (Tag) wrapper.getIndexedValue("favoritesCollection", 1);// obj.getFavoritesCollection() --> iterate to 1

// Set Indexed (List or array)
wrapper.setIndexedValue("authorsList", 1, "new-author");// obj.getAuthorsList().set(1, "new-author");
wrapper.setIndexedValue("chaptersArray", 3, new Chapter());// obj.getChaptersArray()[3] = new Chapter();
```

### Mapped Properties

A mapped property is a **java.util.Map** type whose elements can be individually accessed by an Object-valued key.

```java
// Get Mapped (Map)
String nameInEnglish = (String) wrapper.getMappedValue("namesMap", Locale::ENGLISH);// obj.getNamesMap().get(Locale::ENGLISH);

// Set Mapped (Map)
wrapper.setMappedValue("namesMap", Locale::ENGLISH, "name-in-english");// obj.getNamesMap().put(Locale::ENGLISH, "name-in-english");
```

### Nested Properties

It is possible to concatenate together the property names of the access path, **using "."** separators for simple properties, and **using square brackets "[x]"** for indexed and mapped properties. Any arbitrary level of nesting is allowed.

```java
// Get Nested
String name = (String) wrapper.getValue("book.author.publishersMap[profile].websitesArray[1].owner.name");
// obj.getBook().getAuthor().getPublishersMap().get("profile").getWebsitesArray()[1].getOwner().getName();

// Set Nested
wrapper.setValue("site.projectsList[1].collaboratorsMap[ciaps].name", "Andrea");
// obj.getSite().getProjectsList().get(1).getCollaboratorsMap().get("ciaps").setName("Andrea");
```

### Auto Instancing Option

Default: enabled. If enabled, automatically instances a new object (a no-args constructor is required) or a new List, Map or array, if the nested property has value null in the wrapped object.

```java
Book obj = new Book();
obj.setAuthor(null);// profile and websitesArray are also null

ObjectWrapper wrapper = new ObjectWrapper(obj);
wrapper.setValue("author.profile.websitesArray[1]", "minimalcode.org");
// obj.{new Author()}.{new Profile()}.{new String[1]}[1] = "minimalcode.org";

// Disable it
wrapper.setAutoInstancing(false);
```

### Auto Growing Option

Default: enabled. If enabled, automatically grows-up any out of bounds List or array to accomodate an element in an index position larger than their capacity.

```java
Book obj = new Book();
obj.setChaptersArray(new Chapter[2]);

ObjectWrapper wrapper = new ObjectWrapper(obj);
wrapper.setIndexedValue("chaptersArray", 5, new Chapter());
// obj.getChaptersArray().{grows up to 5 elements}[5] = new Chapter();

// Disable it
wrapper.setAutoGrowing(false);
```

### OutOfBounds Safety Option

Default: enabled. If enabled, always returns a null value instead of throwing an IndexOutOfBoundsException when trying to access to an item in an out of bounds position of a List, array or any type of finite Iterable, behaving like a  Map#get(Object) implementation.

```java
wrapper.getIndexedValue("chaptersArray", 10);
// returns 'null' with obj.getChaptersArray().lenght < 10

wrapper.getIndexedValue("authorsList", 9999999);
// returns 'null' with obj.getAuthorsList().size() < 9999999

// Disable it
wrapper.setOutOfBoundsSafety(false);

```



