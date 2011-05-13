/*
 * Hits.java
 * 
 * This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
 * See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
 * 
 * Copyright 2008-2010 supareno
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supareno.pgnparser.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * The {@code Hits} class is the class that contains a list of {@link Hit}s.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}hit" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author reno
 * @version 1.0
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"hit"
})
@XmlRootElement(name = "hits")
public class Hits implements Comparable<Hits>, Serializable {

	//
	private static final long serialVersionUID = -3111179482133476401L;

	@XmlElement(required = true)
	protected List<Hit> hit;

	/**
	 * Gets the value of the hit property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the hit property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getHit().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Hit }
	 * 
	 * 
	 */
	public List<Hit> getHit() {
		if (hit == null) {
			hit = new ArrayList<Hit>();
		}
		return this.hit;
	}

	/**
	 * Adds an Hit to the Hits object.
	 * @param item the Hit to add.
	 */
	public void addHit(Hit item){
		if (hit == null) {
			hit = new ArrayList<Hit>();
		}
		this.hit.add(item);
	}


	@Override
	public int compareTo(Hits obj) { return CompareToBuilder.reflectionCompare(this, obj); }

	@Override
	public boolean equals(Object obj) { return EqualsBuilder.reflectionEquals(this, obj); }

	@Override
	public int hashCode() { return HashCodeBuilder.reflectionHashCode(this); }

	@Override
	public String toString() { return ToStringBuilder.reflectionToString(this); }
}
