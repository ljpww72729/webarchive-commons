/*
 * Copyright 2016 The International Internet Preservation Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netpreserve.commons.cdx;

import org.netpreserve.commons.util.datetime.DateTimeRange;
import org.netpreserve.commons.util.datetime.VariablePrecisionDateTime;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.netpreserve.commons.cdx.cdxrecord.CdxLineFormat;

import static org.assertj.core.api.Assertions.*;

/**
 *
 */
public class SearchKeyTest {

    @Test
    public void testIncludedString() {
        SearchKey key;

        // Empty key should include all
        key = new SearchKey();
        assertThat(key.included("foo")).isTrue();

        // Exact match
        key = new SearchKey().uri("example.com/foo/index.html");
        assertThat(key.included("(com,example,)/foo/index.html")).isTrue();
        assertThat(key.included("(com,example,)/foo/index.html1")).isFalse();

        // Path wildcard match
        key = new SearchKey().uri("example.com/foo/*");
        assertThat(key.included("(com,example,)/fool/")).isFalse();
        assertThat(key.included("(com,example,)/foo/")).isTrue();
        assertThat(key.included("(com,example,)/foo/index.html")).isTrue();
        assertThat(key.included("(com,example,)/foo")).isFalse();
        assertThat(key.included("(com,example,)/fo/")).isFalse();
        assertThat(key.included("(com,example,host,)/foo/index.html")).isFalse();

        // Host wildcard match
        key = new SearchKey().uri("*example.com/foo/index.html");
        assertThat(key.included("(com,example,)/foo/index.html")).isTrue();
//        assertThat(key.included("(com,example,)/foo/index.html1")).isFalse();
        assertThat(key.included("(com,example,host,)/foo/index.html")).isTrue();
//        assertThat(key.included("(com,example,host,)/foo/index.html1")).isFalse();

        // Range match
        key = new SearchKey().surtUriFrom("(be,halten,)").surtUriTo("(ch,");
        assertThat(key.included("(com,example,)/foo/index.html")).isFalse();
        assertThat(key.included("(be,)")).isFalse();
        assertThat(key.included("(be,halten,)")).isTrue();
        assertThat(key.included("(cg,)")).isTrue();
        assertThat(key.included("(ch,)")).isFalse();
        assertThat(key.included("(be,your-counter,)/robots.txt")).isTrue();

        key = new SearchKey().uri("åå.jalla.øx.com/foo/index.html");
        assertThat(key.included("(ch,)")).isFalse();
    }

    @Test
    public void testIncludedByteBuf() {
        CdxFormat format = CdxLineFormat.CDX09LINE;
        SearchKey key;

        // Empty key should include all
        key = new SearchKey();
        assertThat(key.included(ByteBuffer.wrap("foo".getBytes()), format)).isTrue();

        // Exact match
        key = new SearchKey().uri("example.com/foo/index.html")
                .dateRange(DateTimeRange.ofSingleDate(VariablePrecisionDateTime.of("20000202")));
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html 20000202".getBytes()), format)).isTrue();
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html1".getBytes()), format)).isFalse();
        key = new SearchKey().uri("http://example.com/foo/index.html");
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html".getBytes()), format)).isTrue();
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html1".getBytes()), format)).isFalse();

        // Path wildcard match
        key = new SearchKey().uri("example.com/foo/*");
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/fool/".getBytes()), format)).isFalse();
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/".getBytes()), format)).isTrue();
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html".getBytes()), format)).isTrue();
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo".getBytes()), format)).isFalse();
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/fo/".getBytes()), format)).isFalse();
        assertThat(key.included(ByteBuffer.wrap("(com,example,host,)/foo/index.html".getBytes()), format)).isFalse();

        // Host wildcard match
        key = new SearchKey().uri("*example.com/foo/index.html");
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html".getBytes()), format)).isTrue();
//        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html1")).isFalse();
        assertThat(key.included(ByteBuffer.wrap("(com,example,host,)/foo/index.html".getBytes()), format)).isTrue();
//        assertThat(key.included(ByteBuffer.wrap("(com,example,host,)/foo/index.html1")).isFalse();

        // Range match
        key = new SearchKey().surtUriFrom("(be,halten,)").surtUriTo("(ch,");
        assertThat(key.included(ByteBuffer.wrap("(com,example,)/foo/index.html".getBytes()), format)).isFalse();
        assertThat(key.included(ByteBuffer.wrap("(be,)".getBytes()), format)).isFalse();
        assertThat(key.included(ByteBuffer.wrap("(be,halten,)".getBytes()), format)).isTrue();
        assertThat(key.included(ByteBuffer.wrap("(cg,)".getBytes()), format)).isTrue();
        assertThat(key.included(ByteBuffer.wrap("(ch,)".getBytes()), format)).isFalse();
        assertThat(key.included(ByteBuffer.wrap("(be,your-counter,)/robots.txt".getBytes()), format)).isTrue();

        key = new SearchKey().uri("åå.jalla.øx.com/foo/index.html");
        assertThat(key.included(ByteBuffer.wrap("(ch,)".getBytes()), format)).isFalse();
    }
}
