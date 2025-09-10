package ro.ciprianpascu.sbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ro.ciprianpascu.sbus.Sbus;

/**
 * Response to RGBW Preview (0xF081)
 * Additional content: 1 byte flag → 0xF8 (success) / 0xF5 (fail)
 *
 * Spec reference: LED Driver Protocol - "RGBW 预演 RGBW preview"
 * Response opcode: 0xF081 with 1-byte flag. 
 */
public final class RgbwPreviewResponse extends SbusResponse {

    private boolean success;

    public RgbwPreviewResponse() {
        setFunctionCode(Sbus.WRITE_PREVIEW_COLORS_REQUEST+1);
        setDataLength(1);
    }

    public RgbwPreviewResponse(boolean success) {
        this();
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(success ? 0xF8 : 0xF5);
    }

    @Override
    public void readData(DataInput din) throws IOException {
        int flag = din.readUnsignedByte();
        // Per spec: 0xF8 = success, 0xF5 = fail
        this.success = (flag == 0xF8);
    }

    @Override
    public String toString() {
        return "RgbwPreviewResponse{fc=0xF081, success=" + success + "}";
    }
}
