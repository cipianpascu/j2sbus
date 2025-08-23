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

package ro.ciprianpascu.sbus.facade;

/**
 * Enumeration representing temperature units supported by SBUS devices.
 * This enum provides type-safe temperature unit selection for temperature
 * reading operations.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public enum TemperatureUnit {
    
    /**
     * Fahrenheit temperature scale.
     * Freezing point of water: 32°F
     * Boiling point of water: 212°F
     */
    FAHRENHEIT(0, "°F", "Fahrenheit"),
    
    /**
     * Celsius temperature scale.
     * Freezing point of water: 0°C
     * Boiling point of water: 100°C
     */
    CELSIUS(1, "°C", "Celsius");
    
    private final int value;
    private final String symbol;
    private final String displayName;
    
    /**
     * Constructs a TemperatureUnit enum value.
     *
     * @param value the numeric value used by the SBUS protocol
     * @param symbol the temperature unit symbol (e.g., "°C", "°F")
     * @param displayName the human-readable name of the unit
     */
    TemperatureUnit(int value, String symbol, String displayName) {
        this.value = value;
        this.symbol = symbol;
        this.displayName = displayName;
    }
    
    /**
     * Gets the numeric value used by the SBUS protocol.
     *
     * @return the protocol value for this temperature unit
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Gets the temperature unit symbol.
     *
     * @return the symbol (e.g., "°C", "°F")
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * Gets the human-readable display name.
     *
     * @return the display name (e.g., "Celsius", "Fahrenheit")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Finds a TemperatureUnit by its protocol value.
     *
     * @param value the protocol value to look up
     * @return the corresponding TemperatureUnit
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static TemperatureUnit fromValue(int value) {
        for (TemperatureUnit unit : values()) {
            if (unit.value == value) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown temperature unit value: " + value);
    }
    
    @Override
    public String toString() {
        return displayName + " (" + symbol + ")";
    }
}
