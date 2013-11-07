
/*
 * Copyright 2013 Karl STEIN
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
 */

import java.util.*;

public class ObjectExample {

    public Boolean _oBoolean = true;
    public Byte _oByte = 1;
    public Character _oChar = 'C';
    public Date _oDate = new Date();
    public Double _oDouble = 0.99d;
    public Enumeration _oEnum = Enumeration.ONE;
    public Float _oFloat = 0.1f;
    public Integer _oInteger = 1024;
    public List<String> _oList = new ArrayList<String>();
    public Long _oLong = 2048L;
    public Map<Object, Object> _oMap = new HashMap<Object, Object>();
    public String _oNull;
    public PrimitiveExample _oObject = new PrimitiveExample();
    public Short _oShort = 8;
    public String _oString = "My \r\n<\"String\"> to escape";
    public Set<Object> _oSet = new HashSet<Object>();

    public ObjectExample() {
        _oSet.add("S1");
        _oSet.add("S2");
        _oList.add("L1");
        _oList.add("L2");
        _oMap.put(1, "M1");
        _oMap.put("2nd", new PrimitiveExample());
    }

    private enum Enumeration {
        ONE,
        TWO,
        THREE
    }
}
