/**
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
package ro.ciprianpascu.sbus.net;

import ro.ciprianpascu.sbus.util.SerialParameters;

 /**
 * 
 * SerialConnectionFactory interface.
 * 
 *
 * @author Sami Salonen
 */
public interface SerialConnectionFactory {
	/**
	 * Creates a new {@link SerialConnection} instance.
	 * @param parameters a {@link SerialParameters} instance.
	 * @return a new {@link SerialConnection} instance.
	 */
    public SerialConnection create(SerialParameters parameters);
}