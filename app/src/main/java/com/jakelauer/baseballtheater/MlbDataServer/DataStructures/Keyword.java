package com.jakelauer.baseballtheater.MlbDataServer.DataStructures;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by Jake on 1/14/2017.
 */

@Root(strict = false)
public class Keyword  implements Serializable {
	@Attribute
	public String type;

	@Attribute
	public String value;
}
