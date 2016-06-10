package com.github.sc.scheduler.http;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Common JSON web controller
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface JsonAPI {
}
