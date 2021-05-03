package me.shouheng.startup.compiler.entity;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/** Annotated class. */
public class AnnotatedClass {

    /** Element for annotated class. */
    private Element element;

    public AnnotatedClass(Element element) {
        this.element = element;
    }

    /** Get class name. */
    public ClassName getClassName() {
        return ClassName.get((TypeElement) element);
    }
}
