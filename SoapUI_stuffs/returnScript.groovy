def myTestCase = context.testCase
def runner

propTestStep = myTestCase.getTestStepByName("Properties") // get the Property TestStep

endLoop = propTestStep.getPropertyValue("_stopVal").toString()

if (endLoop.toString().toLowerCase().equals("t"))

{

log.info ("Exit Groovy Data Source Looper")

assert true

}

else

{

testRunner.gotoStepByName("Groovy Script") //setStartStep

}

