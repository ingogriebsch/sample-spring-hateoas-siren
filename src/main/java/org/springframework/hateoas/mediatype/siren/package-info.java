/*-
 * #%L
 * Spring HATEOAS Siren sample
 * %%
 * Copyright (C) 2018 - 2019 Ingo Griebsch
 * %%
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
 * #L%
 */
/**
 * Siren-specific extensions, SPIs and Jackson customizations.
 * <p>
 * TODOs:
 * <ul>
 * <li>Enhance affordance model to provide input (field) parameters?</li>
 * <li>Introduce ResolvableType 2 SirenAction.Field.Type converter (as bean)</li>
 * <li>Use LinkRelationProvider there necessary</li>
 * <li>Rework field creation to own converter</li>
 * <li>Handle templated links (decide there to put based on configuration)</li>
 * <li>Introduce SirenAffordanceModelConverter through Instantiator</li>
 * <li>Enhance converters to use SirenAffordanceModelConverter</li>
 * </ul>
 *
 * @see https://github.com/kevinswiber/siren
 */
@org.springframework.lang.NonNullApi
package org.springframework.hateoas.mediatype.siren;
