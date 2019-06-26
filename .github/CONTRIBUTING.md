### Submitting Pull Requests

We'd love for you to contribute to our source code and to make this package even better than it is
today! Here are the guidelines we'd like you to follow:

 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Coding Rules](#rules)
 - [Commit Message Guidelines](#commit)

## <a name="issue"></a> Found an Issue?

If you find a bug in the source code or a mistake in the documentation, you can help us by
submitting an issue to our GitHub Repository. Even better you can submit a Pull Request
with a fix. But first search if the issue is already described!

If not create a new issue:

* Tell about your environment:
  * node version
  * nativescript version
  * used platform and versionÍ
* Describe your issue
  * describe your steps leading to the issue
  * attach error logs or screenshots
  * if possible provide test case or screenshots

## <a name="feature"></a> Want a Feature?

You can request a new feature by submitting an issue to our [GitHub Repository][github].

Please follow these basic steps to simplify pull request reviews - if you don't you'll probably just be asked to anyway.**

* Please rebase your branch against the current develop, use the **develop** for pull requests
* Please ensure that the test suite passes **and** that code is lint free before submitting a PR by running:
 * ```./mvnw test```
 * Verify plugin is working via docker test ```docker compose up```
* If you've added new functionality, **please** include tests which validate its behaviour
* Make reference to possible [issues](https://github.com/jenkinsci/log-parser-plugin/issues) on PR comment

### Resulting from long experience

* To the largest extent possible, all fields shall be private. Use an IDE to generate the getters and setters.
* If a class has more than one `volatile` member field, it is probable that there are subtle race conditions. Please consider where appropriate encapsulation of the multiple fields into an immutable value object replace the multiple `volatile` member fields with a single `volatile` reference to the value object (or perhaps better yet an `AtomicReference` to allow for `compareAndSet` - if compare-and-set logic is appropriate).
* If it is `Serializable` it shall have a `serialVersionUID` field. Unless code has shipped to users, the initial value of the `serialVersionUID` field shall be `1L`.

### Indentation

1. **Use spaces.** Tabs are banned.
2. **Java blocks are 4 spaces.** JavaScript blocks as for Java. **XML nesting is 2 spaces**

### Field Naming Conventions

1. "hungarian"-style notation is banned (i.e. instance variable names preceded by an 'm', etc)
2. If the field is `static final` then it shall be named in `ALL_CAPS_WITH_UNDERSCORES`.
3. Start variable names with a lowercase letter and use camelCase rather than under_scores.
4. Spelling and abreviations: If the word is widely used in the JVM runtime, stick with the spelling/abreviation in the JVM runtime, e.g. `color` over `colour`, `sync` over `synch`, `async` over `asynch`, etc.
5. It is acceptable to use `i`, `j`, `k` for loop indices and iterators. If you need more than three, you are likely doing something wrong and as such you shall either use full descriptive names or refactor.
6. It is acceptable to use `e` for the exception in a `try...catch` block.
7. You shall never use `l` (i.e. lower case `L`) as a variable name.

### Line Length

To the greatest extent possible, please wrap lines to ensure that they do not exceed 120 characters.

### Maven POM file layout

* The `pom.xml` file shall use the sequencing of elements as defined by the `mvn tidy:pom` command (after any indenting fix-up).
* If you are introducing a property to the `pom.xml` the property must be used in at least two distinct places in the model or a comment justifying the use of a property shall be provided.
* If the `<plugin>` is in the groupId `org.apache.maven.plugins` you shall omit the `<groupId>`.
* All `<plugin>` entries shall have an explicit version defined unless inherited from the parent.

### Java code style

#### Modifiers

* For fields, the order is:
    - public / protected / private
    - static
    - final
    - transient
    - volatile
* For methods, the order is:
    - public / protected / private
    - abstract
    - static
    - final
    - synchronized
    - native
    - strictfp
*  For classes, the order is:
    -  public / protected / private
    -  abstract
    -  static
    -  final
    -  strictfp

#### Imports

* For code in `src/main`:
    - `*` imports are banned.
    - `static` imports are strongly discouraged.
    - `static` `*` imports are discouraged unless code readability is significantly enhanced and the import is restricted to a single class.
* For code in `src/test`:
    - `*` imports of anything other than JUnit classes and Hamcrest matchers are banned.
    - `static` imports of anything other than JUnit classes and Hamcrest matchers are strongly discouraged.
    - `import static org.hamcrest.Matchers.*`, `import static org.junit.Assert.*` are expressly permitted. Any other `static` `*` imports are discouraged unless code readability is significantly enhanced and the import is restricted to a single class.

#### Annotation placement

* Annotations on classes, interfaces, annotations, enums, methods, fields and local variables shall be on the lines immediately preceding the line where modifier(s) (e.g. `public` / `protected` / `private` / `final`, etc) would be appropriate.
* Annotations on method arguments shall, to the largest extent possible, be on the same line as the method argument (and, if present, before the `final` modifier)

#### Javadoc

* Each class shall have a Javadoc comment.
* Each field shall have a Javadoc comment.
* Unless the method is `private`, it shall have a Javadoc comment.
* When a method is overriding a method from a super-class / interface, unless the semantics of the method have changed it is sufficient to document the intent of implementing the super-method's contract with:
    ```
    /**
     * {@inheritDoc}
     */
    @Override
    ```
* Getters and Setters shall have a Javadoc comment. The following is prefered
    ```
    /**
     * The count of widgets
     */
    private int widgetCount;

    /**
     * Returns the count of widgets.
     *
     * @return the count of widgets.
     */
    public int getWidgetCount() {
        return widgetCount;
    }

    /**
     * Sets the count of widgets.
     *
     * @param widgetCount the count of widgets.
     */
    public void setWidgetCount(int widgetCount) {
        this.widgetCount = widgetCount;
    }
    ```
* When adding a new class / interface / etc, it shall have a `@since` doc comment. The version shall be `FIXME` to indicate that the person merging the change should replace the `FIXME` with the next release version number. The fields and methods within a class/interface (but not nested classes) will be assumed to have the `@since` annotation of their class/interface unless a different `@since` annotation is present.

## <a name="rules"></a> Coding Rules

To ensure consistency throughout the source code, keep these rules in mind as you are working:

* All features or bug fixes **must be tested** by one or more [specs][unit-testing].
* All public API methods **must be documented** with jsdoc.


* To the largest extent possible, all fields shall be private. Use an IDE to generate the getters and setters.
* If a class has more than one `volatile` member field, it is probable that there are subtle race conditions. Please consider where appropriate encapsulation of the multiple fields into an immutable value object replace the multiple `volatile` member fields with a single `volatile` reference to the value object (or perhaps better yet an `AtomicReference` to allow for `compareAndSet` - if compare-and-set logic is appropriate).
* If it is `Serializable` it shall have a `serialVersionUID` field. Unless code has shipped to users, the initial value of the `serialVersionUID` field shall be `1L`.


## <a name="commit"></a> Git Commit Guidelines

We're using [Angular Commit Guidelines](https://github.com/angular/angular.js/blob/master/CONTRIBUTING.md#-git-commit-guidelines)
