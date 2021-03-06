/*
 * Copyright 2017-2021 the original author or authors.
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

package org.koin.androidx.scope

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope

/**
 * ScopeFragment
 *
 * Fragment, allow to create & destroy tied Koin scope
 *
 * @author Arnaud Giuliani
 */
abstract class ScopeFragment(
        @LayoutRes contentLayoutId: Int = 0,
        private val initialiseScope: Boolean = true
) : Fragment(contentLayoutId), KoinScopeComponent {

    override val scope: Scope by lazy { fragmentScope() }

    val scopeActivity: ScopeActivity?
        get() = activity as? ScopeActivity

    inline fun <reified T : ScopeActivity> requireScopeActivity(): T = activity as? T
            ?: error("can't get ScopeActivity ${T::class}")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (initialiseScope) {
            getKoin().logger.debug("Open Fragment Scope: $scope")
        }
    }

    /**
     * inject lazily
     * @param qualifier - bean qualifier / optional
     * @param mode
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> KoinScopeComponent.inject(
            qualifier: Qualifier? = null,
            mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
            noinline parameters: ParametersDefinition? = null
    ) = lazy(mode) { get<T>(qualifier, parameters) }

    /**
     * get given dependency
     * @param name - bean name
     * @param scope
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> KoinScopeComponent.get(
            qualifier: Qualifier? = null,
            noinline parameters: ParametersDefinition? = null
    ): T = scope.get(qualifier, parameters)
}