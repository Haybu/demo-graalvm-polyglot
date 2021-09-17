package io.agilehandy.demo;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

@Component
public class PythonInterpolation implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final String language = "python";
        final String filePath = "python/interpolator.py";

        Context context = Context.newBuilder(language)
                .allowAllAccess(true)
                //.allowExperimentalOptions(true)
                //.allowIO(true)
                //.allowNativeAccess(true)
                //.allowPolyglotAccess(PolyglotAccess.ALL)
                //.allowCreateProcess(true)
                //.allowHostClassLookup(name -> true)
                ////.option("python.Executable", "ENV_EXECUTABLE")
                ////.option("python.ForceImportSite", "true")
                .build();

        // to pass something to python side
        //Value pythonBindings = context.getBindings(language);
        //pythonBindings.putMember("foo", "Haytham Mohamed");

        Resource fileResource = new ClassPathResource(filePath);
        File file = fileResource.getFile();
        Source source = Source.newBuilder(language, file).build();

        context.eval(source);

        // to get out of python
        // obtain python class
        Value interpolatorClass = context.getBindings(language).getMember("Interpolator"); //
        // create instance from the python class
        Value interpolatorInstance = interpolatorClass.newInstance();
        // cast to Java interface that exposes the methods
        Interpolator interpolator = interpolatorInstance.as(Interpolator.class);
        // call the method. The method will be routed to python method counterpart
        int sum = interpolator.sum(1, 5, 9);
        System.out.println("sum is " + sum);

        context.close();
    }

}
