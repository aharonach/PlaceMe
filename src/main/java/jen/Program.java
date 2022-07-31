package jen;

import jen.example.*;
import jen.web.entity.Pupil;

public class Program {
    public static void main(String[] args)
            throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        //new HelloWorld().start();
        //new HelloWorldMinimize().start();
        //new OnesCounting().start();
        //new Knapsack().start();
        //new PupilInClasses().start();
        new PupilInClassesEntities().start();
    }
}