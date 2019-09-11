package parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author str1k6rJP
 * @version 1.0.0
 */
@SpringBootApplication
@EnableSwagger2
public class Application{

    public static boolean consoleWriterMode;



    public static void main(String[] args) {

        SpringApplication.run(Application.class,args);

    }

   /* @Override
    public void run(String... args) throws Exception {

        try {
            consoleWriterMode = (args[0].equals("true") || args[0].equals("console")) || (Integer.parseInt(args[0]) == 1) ? true : false;
        } catch (ArrayIndexOutOfBoundsException e){
            consoleWriterMode = false;
            System.out.println("\n\n\n\n\nNo arguments were specified so consoleWriterMode set to false by default\n\n\n\n\n\n");
        }

if (consoleWriterMode){
    System.out.println("\n\n\n\n\n\n\n\n\n\nConsoleWriterMode set to true\n" +
            "You will be able to see intermediate and while-processing data\n\n\n\n\n\n\n\n\n\n");
}
    }*/
}
