/**
 * Package contains object model for http interaction.
 * <p>
 * Naming convention:
 * <ul>
 * <li>{@code *View} - uses for showing data, this classes aren't used as method parameters,
 * only as return types</li>
 * <li>{@code *Form} - uses for passing data to controllers, this classes are used only
 * as method parameters</li>
 * <li>{@code *Dto} - uses for showing and passing data, this classes can be used as method
 * return types and method parameters</li>
 * </ul>
 */
@ParametersAreNonnullByDefault
package com.github.sc.scheduler.http.dto;

import javax.annotation.ParametersAreNonnullByDefault;

// todo remove dependency on scheduler-core!