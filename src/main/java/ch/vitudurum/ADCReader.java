package ch.vitudurum;


import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;


import java.util.Properties;

public class ADCReader implements Runnable{
    //private static int pressCount = 0;
    public static final int DIGITAL_INPUT_PIN =24;
    private static final byte TCA9534_REG_ADDR_OUT_PORT1 = (byte) 0x84;
    private static final byte TCA9534_REG_ADDR_OUT_PORT2 = (byte) 0xc4;
    private static final byte TCA9534_REG_ADDR_CFG = (byte) 0x4B;
    I2C tca9534Dev;
    boolean up=false;
    Pong pong;
    int ADCResolution=255;
    private static final int PIN_BUTTON_SHOT1 = 23; // PIN 18 = BCM 24
    private static final int PIN_BUTTON_SHOT2 = 25; // PIN 18 = BCM 24
    private static final int PIN_BUTTON_RESTART = 24; // PIN 18 = BCM 24
    private static final int PIN_LED = 22; // PIN 15 = BCM 22
    Context pi4j;


    public ADCReader(Pong pong) {
        this.pong=pong;
        try {
            initIC2();
            initGPIO();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        };
    }
    public void initIC2()
    {
        Context pi4j = Pi4J.newAutoContext();
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("7830").bus(1).device(0x4B).build();
        try (I2C tca9534Dev = i2CProvider.create(i2cConfig)) {
            this.tca9534Dev=tca9534Dev;
            int config = tca9534Dev.readRegister(TCA9534_REG_ADDR_CFG);
            if (config < 0)
                throw new IllegalStateException(
                        "Failed to read configuration from address 0x" + String.format("%02x", TCA9534_REG_ADDR_CFG));

            System.out.println("IC2 Ready");
            up=true;

        }
        catch (Exception e)
        {
            System.out.println("Error connecting adc:"+e.getMessage());
        }
    }
    public void  initGPIO(){

        System.out.println("Initiating GPIO...");
        // Create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
        var pi4j = Pi4J.newAutoContext();

        var buttonConfigS1 = DigitalInput.newConfigBuilder(pi4j)
                .id("PIN_BUTTON_SHOT1")
                .name("Press button")
                .address(PIN_BUTTON_SHOT1)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L)
                .provider("pigpio-digital-input");

        var buttonConfigS2 = DigitalInput.newConfigBuilder(pi4j)
                .id("PIN_BUTTON_SHOT2")
                .name("Press button")
                .address(PIN_BUTTON_SHOT2)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L)
                .provider("pigpio-digital-input");

        var buttonConfigRestart = DigitalInput.newConfigBuilder(pi4j)
                .id("PIN_BUTTON_RESTART")
                .name("Press button")
                .address(PIN_BUTTON_RESTART)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L)
                .provider("pigpio-digital-input");


        var button_shot1 = pi4j.create(buttonConfigS1);
        var button_shot2 = pi4j.create(buttonConfigS2);
        var button_restart = pi4j.create(buttonConfigRestart);

        button_shot1.addListener(e -> {
            if (e.state() == DigitalState.LOW) {
                System.out.println("Button Shot P1 was pressed");
                pong.getBall().setMode(Ball.MODE_ANSPIEL_1);
            }
        });

        button_shot2.addListener(e -> {
            if (e.state() == DigitalState.LOW) {
                System.out.println("Button Shot P2 was pressed");
                pong.getBall().setMode(Ball.MODE_ANSPIEL_2);
            }
        });

        button_restart.addListener(e -> {
            if (e.state() == DigitalState.LOW) {
                System.out.println("Button Restart was pressed");
                pong.getBall().startGame();
            }
        });

    }
    /*
    public void  initGPIOOK(){

        System.out.println("Initiating GPIO...");
        // Create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
        final var console = new Console();

        // Print program title/header
        console.title("<-- The Pi4J Project -->", "Minimal Example project");

        // ************************************************************
        //
        // WELCOME TO Pi4J:
        //
        // Here we will use this getting started example to
        // demonstrate the basic fundamentals of the Pi4J library.
        //
        // This example is to introduce you to the boilerplate
        // logic and concepts required for all applications using
        // the Pi4J library.  This example will do use some basic I/O.
        // Check the pi4j-examples project to learn about all the I/O
        // functions of Pi4J.
        //
        // ************************************************************

        // ------------------------------------------------------------
        // Initialize the Pi4J Runtime Context
        // ------------------------------------------------------------
        // Before you can use Pi4J you must initialize a new runtime
        // context.
        //
        // The 'Pi4J' static class includes a few helper context
        // creators for the most common use cases.  The 'newAutoContext()'
        // method will automatically load all available Pi4J
        // extensions found in the application's classpath which
        // may include 'Platforms' and 'I/O Providers'
        var pi4j = Pi4J.newAutoContext();

        // ------------------------------------------------------------
        // Output Pi4J Context information
        // ------------------------------------------------------------
        // The created Pi4J Context initializes platforms, providers
        // and the I/O registry. To help you to better understand this
        // approach, we print out the info of these. This can be removed
        // from your own application.
        // OPTIONAL
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);

        // Here we will create I/O interfaces for a (GPIO) digital output
        // and input pin. We define the 'provider' to use PiGpio to control
        // the GPIO.
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led")
                .name("LED Flasher")
                .address(PIN_LED)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        var led = pi4j.create(ledConfig);

        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("button")
                .name("Press button")
                .address(PIN_BUTTON)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L)
                .provider("pigpio-digital-input");
        var button = pi4j.create(buttonConfig);
        button.addListener(e -> {
            if (e.state() == DigitalState.LOW) {
                pressCount++;
                console.println("Button was pressed for the " + pressCount + "th time");
            }
        });

        // OPTIONAL: print the registry
        PrintInfo.printRegistry(console, pi4j);

        while (pressCount < 5) {
            if (led.equals(DigitalState.HIGH)) {
                console.println("LED low");
                led.low();
            } else {
                console.println("LED high");
                led.high();
            }
            try {
                Thread.sleep(500 / (pressCount + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        // ------------------------------------------------------------
        // Terminate the Pi4J library
        // ------------------------------------------------------------
        // We we are all done and want to exit our application, we must
        // call the 'shutdown()' function on the Pi4J static helper class.
        // This will ensure that all I/O instances are properly shutdown,
        // released by the the system and shutdown in the appropriate
        // manner. Terminate will also ensure that any background
        // threads/processes are cleanly shutdown and any used memory
        // is returned to the system.

        // Shutdown Pi4J
        pi4j.shutdown();


    }
*/
    public int getADCValue(int id) {
        if (id == 0)
            return tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT1);
        else
            return tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT2);

    }
    public boolean isADCUp()
    {
        return up;
    }


    public static void main(String[] args) throws Exception {

        Context pi4j = Pi4J.newAutoContext();
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("7830").bus(1).device(0x4B).build();
        try (I2C tca9534Dev = i2CProvider.create(i2cConfig)) {

            int config = tca9534Dev.readRegister(TCA9534_REG_ADDR_CFG);
            if (config < 0)
                throw new IllegalStateException(
                        "Failed to read configuration from address 0x" + String.format("%02x", TCA9534_REG_ADDR_CFG));

            System.out.println("IC2 Ready");
            //System.out.println("IC2 Write");
            //tca9534Dev.writeRegister(TCA9534_REG_ADDR_CFG, (byte) 0x84);
            System.out.println("Try reading");
            //byte currentState = (byte) tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT);
            int zahl1 = tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT1);
            int zahl2 = tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT2);
            //int zahl1=tca9534Dev.readRegisterWord(TCA9534_REG_ADDR_OUT_PORT);
            System.out.println("Value-1:" + zahl1);
            System.out.println("Value-2:" + zahl2);
            //System.out.println("Value:"+zahl1);
            System.out.println("Finishing");


        }
    }

    public static byte setPin(byte currentState, int pin, I2C tca9534Dev, boolean high) {
        byte newState;
        if (high)
            newState = (byte) (currentState | (1 << pin));
        else
            newState = (byte) (currentState & ~(1 << pin));

        System.out.println("Setting TCA9534 to new state " + asBinary(newState));
        tca9534Dev.writeRegister(TCA9534_REG_ADDR_OUT_PORT1, newState);
        return newState;
    }

    public static String asBinary(byte b) {
        StringBuilder sb = new StringBuilder();

        sb.append(((b >>> 7) & 1));
        sb.append(((b >>> 6) & 1));
        sb.append(((b >>> 5) & 1));
        sb.append(((b >>> 4) & 1));
        sb.append(((b >>> 3) & 1));
        sb.append(((b >>> 2) & 1));
        sb.append(((b >>> 1) & 1));
        sb.append(((b >>> 0) & 1));

        return sb.toString();
    }

    @Override
    public void run() {
        int z1=0;
        int temp=0;
        int z2=0;
        int diff=0;
         try {
            while (isADCUp()) {
                //move();
                temp=getADCValue(0);
                diff = Math.abs(temp-z1);
                if (temp!=z1 && diff <50)
                {
                    pong.getBall().getPaddle1().setPaddleValue(temp*Pong.gHeight/ADCResolution);
                    z1=temp;
                }
                temp=getADCValue(1);
                diff = Math.abs(temp-z2);
                if (temp!=z2 && diff < 50)
                {
                    pong.getBall().getPaddle2().setPaddleValue(temp*Pong.gHeight/ADCResolution);
                    z2=temp;
                }

                Thread.sleep(50);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}