package jen;

import jen.example.*;
import jen.web.entity.Pupil;
import jen.web.entity.Template;

public class Program {
    public static void main(String[] args)
            throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, Template.AttributeAlreadyExistException {
        //new HelloWorld().start();
        //new HelloWorldMinimize().start();
        //new OnesCounting().start();
        //new Knapsack().start();
        //new PupilInClasses().start();
        new PupilInClassesEntities().start();
    }
}