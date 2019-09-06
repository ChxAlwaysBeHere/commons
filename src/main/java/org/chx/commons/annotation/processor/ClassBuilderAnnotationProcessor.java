package org.chx.commons.annotation.processor;

import com.squareup.javapoet.*;
import org.chx.commons.annotation.ClassBuilder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * annotation processor for {@link ClassBuilder}
 * TODO ... to be continued
 *
 * @author chenxi
 * @date 2019-09-05
 */
@SupportedAnnotationTypes("org.chx.commons.annotation.ClassBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ClassBuilderAnnotationProcessor extends AbstractProcessor {

    private Filer filer;

    private Types typesHelper;

    private Elements elementsHelper;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();
        this.typesHelper = processingEnv.getTypeUtils();
        this.elementsHelper = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ClassBuilder.class);
        elements.stream()
                .filter(ClassBuilderAnnotationProcessor::isInstanceClass)
                .forEach(element -> {
                    PackageElement packageElement = elementsHelper.getPackageOf(element);
                    String packageName = packageElement.getQualifiedName().toString();

                    TypeElement typeElement = (TypeElement) element;
                    Name className = typeElement.getSimpleName();
                    TypeName typeName = TypeName.get((typeElement).asType());

                    ClassBuilder annotation = element.getAnnotation(ClassBuilder.class);
                    String builderName = (isValidNaming(annotation.value()) ? annotation.value() : className + "Builder");
                    ClassName builderClassName = ClassName.get(packageName, builderName);

                    FieldSpec fieldSpec = FieldSpec.builder(typeName, "instance", Modifier.PRIVATE)
                            .initializer("new $T()", typeName)
                            .build();

                    List<MethodSpec> methodSpecs = typeElement.getEnclosedElements().stream()
                            .filter(elem -> elem.getKind() == ElementKind.METHOD)
                            .map(elem -> {
                                ExecutableElement methodElement = (ExecutableElement) elem;
                                String methodName = methodElement.getSimpleName().toString();

                                List<? extends VariableElement> parameters = methodElement.getParameters();
                                if (parameters.size() != 1) {
                                    return null;
                                }
                                if (!isSetterMethod(methodName)) {
                                    return null;
                                }

                                VariableElement parameter = parameters.get(0);

                                String field = parameter.getSimpleName().toString();
                                MethodSpec setterMethodSpec = MethodSpec.methodBuilder(field)
                                        .addModifiers(Modifier.PUBLIC)
                                        .addParameter(TypeName.get(parameter.asType()), field)
                                        .addStatement("instance.$L($L);return this", methodName, field)
                                        .returns(builderClassName)
                                        .build();
                                return setterMethodSpec;
                            }).filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    MethodSpec newMethodSpec = MethodSpec.methodBuilder("create")
                            .addStatement("return new $T()", builderClassName)
                            .returns(builderClassName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .build();

                    MethodSpec buildMethodSpec = MethodSpec.methodBuilder("build")
                            .addStatement("return instance")
                            .returns(typeName)
                            .addModifiers(Modifier.PUBLIC)
                            .build();

                    TypeSpec typeSpec = TypeSpec.classBuilder(builderName)
                            .addModifiers(Modifier.PUBLIC)
                            .addField(fieldSpec)
                            .addMethod(constructorMethodSpec)
                            .addMethod(newMethodSpec)
                            .addMethod(buildMethodSpec)
                            .addMethods(methodSpecs)
                            .build();

                    JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
                    try {
                        javaFile.writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return true;
    }

    private static boolean isInstanceClass(Element element) {
        // filter interfaces or enums
        if (!Objects.equals(ElementKind.CLASS, element.getKind())) {
            return false;
        }
        Set<Modifier> modifiers = element.getModifiers();
        // filter abstract class
        return !modifiers.contains(Modifier.ABSTRACT);
    }

    private static boolean isValidNaming(String naming) {
        return Objects.nonNull(naming) && naming.matches("^[a-zA-Z_$][a-zA-Z0-9_$]+$");
    }

    private static boolean isSetterMethod(String methodName) {
        return Objects.nonNull(methodName) && methodName.startsWith("set") && methodName.length() > 3;
    }

}
