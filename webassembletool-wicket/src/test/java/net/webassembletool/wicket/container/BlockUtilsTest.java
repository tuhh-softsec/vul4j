/*
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
 *
 */
package net.webassembletool.wicket.container;

import net.webassembletool.wicket.utils.BlockUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for :
 * 
 * @see net.webassembletool.wicket.utils.BlockUtils
 * 
 * @author Nicolas Richeton
 */
public class BlockUtilsTest  {

	/**
	 * Test for
	 * 
	 * @see net.webassembletool.wicket.utils.BlockUtils#discardTags(String, int)
	 * 
	 */
	@Test
	public void testDiscardTags() {
		String content, expected;

		content = "<head>Test</head>";
		expected = "Test";
		Assert.assertEquals(expected, BlockUtils.discardTags(content, 1));

		content = "<head>Test</head>";
		expected = "Test";
		Assert.assertEquals(expected, BlockUtils.discardTags(content, 2));

		content = "<head>Test</head>";
		expected = "<head>Test</head>";
		Assert.assertEquals(expected, BlockUtils.discardTags(content, 0));

		content = "<head><script type=''>Test</script></head>";
		expected = "<script type=''>Test</script>";
		Assert.assertEquals(expected, BlockUtils.discardTags(content, 1));

		content = " aaa ef jfk <head><script type=''>Test</script></head> efjk jzff ";
		expected = "<script type=''>Test</script>";
		Assert.assertEquals(expected, BlockUtils.discardTags(content, 1));

	}
}
