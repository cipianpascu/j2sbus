/**
 * Copyright 2002-2010 jamod development team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***/

package ro.ciprianpascu.sbus.msg;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;

/**
 * Abstract class implementing a {@link SbusRequest}.
 * This class provides specialised implementations with
 * the functionality they have in common.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public abstract class SbusRequest extends SbusMessageImpl {

    /**
     * Constructs a new SbusRequest instance.
     * Initializes the request with default values inherited from SbusMessageImpl.
     */
    public SbusRequest() {
        super();
    }

    /**
     * Returns the {@link SbusResponse} that
     * represents the answer to this {@link SbusRequest}.
     * The implementation should take care about assembling
     * the reply to this {@link SbusRequest}.
     *
     * @param procImg the process image implementation to create the response from
     * @return the corresponding {@link SbusResponse}.
     */
    public abstract SbusResponse createResponse(ProcessImageImplementation procImg);

    /**
     * Factory method for creating exception responses with the
     * given exception code.
     *
     * @param EXCEPTION_CODE the code of the exception.
     * @return a SbusResponse instance representing the exception
     *         response.
     */
    public SbusResponse createExceptionResponse(int EXCEPTION_CODE) {
        ExceptionResponse response = new ExceptionResponse(this.getFunctionCode(), EXCEPTION_CODE);
        response.setSourceSubnetID(this.getSourceSubnetID());
        response.setSourceUnitID(this.getSourceUnitID());
        response.setSourceDeviceType(this.getSourceDeviceType());
        response.setSubnetID(this.getSubnetID());
        response.setUnitID(this.getUnitID());
        return response;
    }

    /**
     * Factory method creating the required specialized {@link SbusRequest}
     * instance.
     *
     * @param functionCode the function code of the request as {@link int}.
     * @return a SbusRequest instance specific for the given function type.
     */
    public static SbusRequest createSbusRequest(int functionCode) {
        SbusRequest request = null;

        switch (functionCode) {
            case Sbus.READ_STATUS_CHANNELS_REQUEST:
                request = new ReadStatusChannelsRequest();
                break;
            case Sbus.READ_TEMPERATURE_REQUEST:
                request = new ReadTemperatureRequest();
                break;
            case Sbus.READ_DRY_CONNECTOR_REQUEST:
                request = new ReadDryChannelsRequest();
                break;
            case Sbus.READ_NINE_IN_ONE_STATUS_REQUEST:
                request = new ReadNineInOneStatusRequest();
                break;
            case Sbus.WRITE_SINGLE_CHANNEL_REQUEST:
                request = new WriteSingleChannelRequest(false);
                break;
            default:
                request = new IllegalFunctionRequest(functionCode);
                break;
        }
        return request;
    }

    /**
     * Checks if this request is a fire-and-forget type request that doesn't require a response.
     * This includes write operations like WRITE_MULTIPLE_REGISTERS, WRITE_SINGLE_CHANNEL_REQUEST,
     * and WRITE_RGBW_REQUEST.
     *
     * @return true if this is a fire-and-forget request, false otherwise
     */
    public boolean isFireAndForget() {
        boolean result = false;
        switch (this.getFunctionCode()) {
            case Sbus.WRITE_CUSTOM_COLORS_REQUEST:
            case Sbus.WRITE_SINGLE_CHANNEL_REQUEST:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
}
