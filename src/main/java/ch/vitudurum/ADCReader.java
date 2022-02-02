package ch.vitudurum;


import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.util.Console;


import java.util.Properties;

public class ADCReader implements Runnable{
    private static int pressCount = 0;
    public static final int DIGITAL_INPUT_PIN =24;

    private static final byte TCA9534_REG_ADDR_OUT_PORT1 = (byte) 0x84;
    private static final byte TCA9534_REG_ADDR_OUT_PORT2 = (byte) 0xc4;
    //private static final byte TCA9534_REG_ADDR_CFG = 0x03;
    private static final byte TCA9534_REG_ADDR_CFG = (byte) 0x4B;
    I2C tca9534Dev;
    boolean up=false;
    Pong pong;
    int ADCResolution=255;
    Context pi4j;

    public ADCReader(Pong pong) {
        this.pong=pong;
        pi4j = Pi4J.newAutoContext();
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
            System.out.println("Error connecting adc...");
        }
        if (up) initGPIO();
    }
    public void  initGPIO()
    {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        // TODO :: REMOVE TEMPORARY PROPERTIES WHEN NATIVE PIGPIO LIB IS READY
        // this temporary property is used to tell
        // PIGPIO which remote Raspberry Pi to connect to
        System.setProperty("pi4j.host", "rpi3bp.savage.lan");

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
        final var console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "Basic Digital Input Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // Initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        var pi4j = Pi4J.newAutoContext();

        // create a digital input instance using the default digital input provider
        // we will use the PULL_DOWN argument to set the pin pull-down resistance on this GPIO pin
        var config = DigitalInput.newConfigBuilder(pi4j)
                //.id("my-digital-input")
                .address(DIGITAL_INPUT_PIN)
                .pull(PullResistance.PULL_DOWN)
                .build();

        // get a Digital Input I/O provider from the Pi4J context
        DigitalInputProvider digitalInputProvider = pi4j.provider("pigpio-i2c");

        var input = digitalInputProvider.create(config);

        // setup a digital output listener to listen for any state changes on the digital input
        input.addListener(event -> {
            Integer count = (Integer) event.source().metadata().get("count").value();
            console.println(event + " === " + count);
        });

        // lets read the analog output state
        console.print("THE STARTING DIGITAL INPUT STATE IS [");
        console.println(input.state() + "]");

        console.println("CHANGE INPUT STATES VIA I/O HARDWARE AND CHANGE EVENTS WILL BE PRINTED BELOW:");

        // wait (block) for user to exit program using CTRL-C
        try {
            console.waitForExit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // shutdown Pi4J
        console.println("ATTEMPTING TO SHUTDOWN/TERMINATE THIS PROGRAM");
        pi4j.shutdown();
    }
    public void initGPIO2()
    {

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
        final var console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "Basic Digital Input Example From Properties");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // Initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        //var pi4j = Pi4J.newAutoContext();

        // create a properties map with ".address" and ".shutdown" properties for the digital output configuration
        Properties properties = new Properties();
        properties.put("id", "my_digital_input");
        properties.put("address", DIGITAL_INPUT_PIN);
        properties.put("pull", "UP");
        properties.put("name", "MY-DIGITAL-INPUT");

        // create a digital input instance using the default digital input provider
        // we will use the PULL_DOWN argument to set the pin pull-down resistance on this GPIO pin
        var config = DigitalInput.newConfigBuilder(pi4j)
                .load(properties)
                .build();

        var input = pi4j.din().create(config);

        // setup a digital output listener to listen for any state changes on the digital input
        input.addListener(console::print);

        // lets read the digital output state
        console.print("DIGITAL INPUT [");
        console.print(input);
        console.print("] STATE IS [");
        console.println(input.state() + "]");

        console.print("DIGITAL INPUT [");
        console.print(input);
        console.print("] PULL RESISTANCE IS [");
        console.println(input.pull() + "]");

        console.println();
        console.println("CHANGE INPUT STATES VIA I/O HARDWARE AND CHANGE EVENTS WILL BE PRINTED BELOW:");

        // wait (block) for user to exit program using CTRL-C
        try {
            console.waitForExit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // shutdown Pi4J
        console.println("ATTEMPTING TO SHUTDOWN/TERMINATE THIS PROGRAM");
        pi4j.shutdown();

    }
    public void initGPIOOld()
    {
        var pi4j = Pi4J.newAutoContext();
        Properties properties = new Properties();
        properties.put("id", "my_digital_input");
        properties.put("address", 24);
        properties.put("pull", "UP");
        properties.put("name", "MY-DIGITAL-INPUT");

        var config = DigitalInput.newConfigBuilder(pi4j)
                .load(properties)
                .build();

        var input = pi4j.din().create(config);

        input.addListener(e -> {
            System.out.println("Button happens");
            if (e.state() == DigitalState.HIGH) {
                System.out.println("Button is pressed");
            }
        });
        System.out.println("Button ready");
    }

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
/*
            if (config != 0x00) {
                System.out.println("TCA9534 is not configured as OUTPUT, setting register 0x" + String
                        .format("%02x", TCA9534_REG_ADDR_CFG) + " to 0x00");
                currentState = 0x00;
                tca9534Dev.writeRegister(TCA9534_REG_ADDR_OUT_PORT, currentState);
                tca9534Dev.writeRegister(TCA9534_REG_ADDR_CFG, (byte) 0x00);
            }

            // bit 8, is pin 1 on the board itself, so set pins in reverse:
            currentState = setPin(currentState, 8, tca9534Dev, true);
            Thread.sleep(500L);
            currentState = setPin(currentState, 8, tca9534Dev, false);
            Thread.sleep(500L);

            currentState = setPin(currentState, 7, tca9534Dev, true);
            Thread.sleep(500L);
            currentState = setPin(currentState, 7, tca9534Dev, false);
            Thread.sleep(500L);

*/

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
         try {
            while (isADCUp()) {
                //move();
                temp=getADCValue(0);
                if (temp!=z1)
                {
                    pong.getBall().getPaddle1().setPaddleValue(temp*Pong.gHeight/ADCResolution);
                    z1=temp;
                }
                temp=getADCValue(1);
                if (temp!=z2)
                {
                    pong.getBall().getPaddle2().setPaddleValue(temp*Pong.gHeight/ADCResolution);
                    z2=temp;
                }

                Thread.sleep(10);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}