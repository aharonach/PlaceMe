import {Link} from "react-router-dom";
import React from "react";

export function extractListFromAPI(object, property, mapCallback = null ) {
    const embedded = object?._embedded;

    if ( ! ( embedded && embedded[property] ) ) {
        return [];
    }

    if ( mapCallback ) {
        return embedded[property].map( mapCallback );
    }

    return embedded[property];
}

export function prepareCheckboxGroup(valueProperty, labelProperty) {
    return item => Object.assign({}, { value: item[valueProperty], label: item[labelProperty] });
}

export function camelCaseToWords( text ) {
    const result = text.replace(/([A-Z])/g, " $1");
    return result.charAt(0).toUpperCase() + result.slice(1);
}

export function getFieldIds(fields) {
    return fields.map( field => field.id );
}

export function getDefaultValuesByFields(fields, values) {
    const fieldIds = getFieldIds(fields);
    return Object.fromEntries( Object.entries(values).filter( entry => fieldIds.includes(entry[0]) ));
}

export function setFormValues(form, values) {
    Object.keys(values).forEach(key => form.setValue(key, values[key]));
}

export function objectIsEmpty(obj) {
    return obj && Object.entries(obj).length === 0;
}

export function objectLinkList(linkTo, objects, displayField, delimiter = ', ') {
    return objects ? objects.map( (obj, i) => <React.Fragment key={obj.id}>{i > 0 && delimiter}<Link to={`/${linkTo}/${obj.id}`}>{obj[displayField]}</Link></React.Fragment> ) : '';
}

export function idLinkList(linkTo, ids, delimiter = ', ') {
    return ids ? ids.map( (id, i) => <React.Fragment key={id}>{i > 0 && delimiter}<Link to={`/${linkTo}/${id}`}>{id}</Link></React.Fragment> ) : '';
}

export function boolToString(bool){
    return bool ? 'Yes' : 'No';
}