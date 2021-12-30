package ch.vitudurum;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

public class ADCReader
{
    public static void main(String[] args) throws Exception
    {
        // Create I2C bus
        I2CBus Bus = I2CFactory.getInstance(I2CBus.BUS_1);
        // Get I2C device, ADS7830 I2C address is 0x48(72)
        I2CDevice device = Bus.getDevice(0x48);

        // Differential inputs, Channel-0, Channel-1 selected
        device.write((byte)0x04);
        Thread.sleep(500);

        // Read 1 byte of data
        byte[] data = new byte[1];
        data[0] = (byte)device.read();

        // Convert the data
        int raw_adc = data[0];

        // Output data to screen
        System.out.printf("Digital value of analog input : %d %n", raw_adc);
    }
}