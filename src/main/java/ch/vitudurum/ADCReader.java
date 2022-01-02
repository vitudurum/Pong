package ch.vitudurum;


import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

public class ADCReader {

    private static final byte TCA9534_REG_ADDR_OUT_PORT1 = (byte) 0x84;
    private static final byte TCA9534_REG_ADDR_OUT_PORT2 = (byte) 0xc4;
    //private static final byte TCA9534_REG_ADDR_CFG = 0x03;
    private static final byte TCA9534_REG_ADDR_CFG = (byte)0x4B;
    I2C tca9534Dev;
    I2CConfig i2cConfig;
    I2CProvider i2CProvider;
    Context pi4j;

    public ADCReader() {
        pi4j = Pi4J.newAutoContext();
        i2CProvider = pi4j.provider("linuxfs-i2c");
        i2cConfig = I2C.newConfigBuilder(pi4j).id("7830").bus(1).device(0x4B).build();
        tca9534Dev = i2CProvider.create(i2cConfig);

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
            int zahl1=tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT1);
            int zahl2=tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT2);
            //int zahl1=tca9534Dev.readRegisterWord(TCA9534_REG_ADDR_OUT_PORT);
            System.out.println("Value-1:"+zahl1);
            System.out.println("Value-2:"+zahl2);
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
    public int getPosition(int player)
    {
        int zahl=250;
        if (player == 1) return tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT1);
        return zahl;
    }
}