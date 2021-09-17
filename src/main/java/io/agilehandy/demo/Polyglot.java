/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.agilehandy.demo;

import java.io.File;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author : Haytham Mohamed
 * @created : Saturday 5/1/21
 **/

//@Component
public class Polyglot implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		final String language = "python";
		final String filePath = "python/example.py";

		Context context = Context.newBuilder().allowIO(true).build();
		Value valueBindings = context.getBindings(language);

		valueBindings.putMember("foo", "Haytham Mohamed");

		Resource fileResource = new ClassPathResource(filePath);
		File file = fileResource.getFile();
		Source source = Source.newBuilder(language, file).build();
		context.eval(source);
		context.close();
	}
}
