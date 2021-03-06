/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.stratio.crossdata.common.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.stratio.crossdata.common.manifest.FunctionTypeHelper;

public class FunctionTypeTest {


    @Test
    public void testCheckValidSignatures(){
        //Stored, Query
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int]", "Tuple[Int]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[]", "Tuple[]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[]", "Tuple[Int*]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[]", "Tuple[Any*]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int,Any*]", "Tuple[Int]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Any*]", "Tuple[]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int]", "Tuple[Int*]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int]", "Tuple[Int, Text*]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int*]", "Tuple[Int,Any,Int]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Any*]", "Tuple[Int,Any,Int]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int*]", "Tuple[Any]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Any]", "Tuple[Int]"));
        Assert.assertTrue(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int]", "Tuple[Any]"));

    }


    @Test
    public void testCheckWrongSignatures(){
        //Stored, Query
        Assert.assertFalse(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[]", "Tuple[Int]"));
        Assert.assertFalse(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[]", "Tuple[Any]"));
        Assert.assertFalse(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int,Any,Text]", "Tuple[Int,Any,Int]"));
        Assert.assertFalse(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int*]", "Tuple[Text]"));
        Assert.assertFalse(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Text*]", "Tuple[Int*]"));
        Assert.assertFalse(FunctionTypeHelper.checkInputSignatureCompatibility("Tuple[Int,Any,Text]","Tuple[Int,Int,Int]"));
    }

}
