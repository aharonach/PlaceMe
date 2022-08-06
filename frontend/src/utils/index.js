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