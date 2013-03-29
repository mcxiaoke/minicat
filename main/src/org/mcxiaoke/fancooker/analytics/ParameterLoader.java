// Copyright 2011 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.mcxiaoke.fancooker.analytics;


/**
 * Interface for loading parameters used by the EasyTracker library.
 */
interface ParameterLoader {

  /**
   * Look up the string value for the resource whose name is key.
   *
   * @param key the key for the string resource we're seeking
   * @return the string value, or null if not found
   */
  String getString(String key);
  
  /**
   * Look up the boolean value represented by the string resource whose name is
   * key.
   *
   * @param key the key for the string resource we're seeking
   * @return true if the key is found and is a string 'true'.  Case is ignored.
   */
  boolean getBoolean(String key);
  
  /**
   * Look up the integer value represented by the string resource whose name is
   * key.
   *
   * @param key the key for the string resource we're seeking
   * @param defaultValue the value to return if the key isn't found
   * @return the int value found, or the defaultValue if the key wasn't found
   *     or couldn't be parsed into an int.
   */
  int getInt(String key, int defaultValue);
}
