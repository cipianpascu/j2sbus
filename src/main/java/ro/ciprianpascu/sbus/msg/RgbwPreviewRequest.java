package ro.ciprianpascu.sbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;

/**
 * RGBW Preview (0xF080)
 * Additional content: 6 bytes = [R(0..100), G(0..100), B(0..100), W(0..100), runTimeHi, runTimeLo]
 * Response opcode: 0xF081 (see RgbwPreviewResponse)
 *
 * Spec reference: LED Driver Protocol - "RGBW 预演 RGBW preview"
 * Request 0xF080, Response 0xF081. 
 */
public final class RgbwPreviewRequest extends SbusRequest {

    private int red;     // 0..100
    private int green;   // 0..100
    private int blue;    // 0..100
    private int white;   // 0..100
    private int runTime; // 0..65535 (milliseconds or device-ticks per spec)

    public RgbwPreviewRequest() {
        setFunctionCode(Sbus.WRITE_PREVIEW_COLORS_REQUEST);
        setDataLength(6); // 4x level + 2-byte runtime
    }

    public RgbwPreviewRequest(int red, int green, int blue, int white, int runTime) {
        this();
        setLevels(red, green, blue, white);
        setRunTime(runTime);
    }

    public void setLevels(int r, int g, int b, int w) {
        this.red   = clamp01(r);
        this.green = clamp01(g);
        this.blue  = clamp01(b);
        this.white = clamp01(w);
    }

    public void setRunTime(int runTime) {
        if (runTime < 0) runTime = 0;
        if (runTime > 0xFFFF) runTime = 0xFFFF;
        this.runTime = runTime;
    }

    public int getRed()   { return red; }
    public int getGreen() { return green; }
    public int getBlue()  { return blue; }
    public int getWhite() { return white; }
    public int getRunTime() { return runTime; }

    @Override
    public void writeData(DataOutput dout) throws IOException {
        // 4 x levels (unsigned bytes 0..100)
        dout.writeByte(red   & 0xFF);
        dout.writeByte(green & 0xFF);
        dout.writeByte(blue  & 0xFF);
        dout.writeByte(white & 0xFF);
        // 2-byte runtime (big-endian: hi, lo)
        dout.writeByte((runTime >> 8) & 0xFF);
        dout.writeByte(runTime & 0xFF);
    }

    @Override
    public void readData(DataInput din) throws IOException {
        // Request rarely needs readData(), but implement for completeness
        this.red   = din.readUnsignedByte();
        this.green = din.readUnsignedByte();
        this.blue  = din.readUnsignedByte();
        this.white = din.readUnsignedByte();
        int hi = din.readUnsignedByte();
        int lo = din.readUnsignedByte();
        this.runTime = (hi << 8) | lo;
    }

    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        // For local/server emulation: always return SUCCESS (0xF8).
        RgbwPreviewResponse resp = new RgbwPreviewResponse(true);
        // Mirror addressing & opcode (+1)
        resp.setSourceSubnetID(getSourceSubnetID());
        resp.setSourceUnitID(getSourceUnitID());
        resp.setSourceDeviceType(getSourceDeviceType());
        resp.setSubnetID(getSubnetID());
        resp.setUnitID(getUnitID());
        resp.setFunctionCode(0xF081);
        return resp;
    }

    private static int clamp01(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }

    @Override
    public String toString() {
        return "RgbwPreviewRequest{fc=0xF080, R=" + red + ", G=" + green + ", B=" + blue + ", W=" + white +
               ", runTime=" + runTime + "}";
    }
}
