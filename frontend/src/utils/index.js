export function ExtractList( object, property, mapCallback = null ) {
    const embedded = object?._embedded;

    if ( ! ( embedded && embedded[property] ) ) {
        return null;
    }

    if ( mapCallback ) {
        return embedded[property].map( mapCallback );
    }

    return embedded[property];
}

export function PrepareCheckboxGroup(valueProperty, labelProperty) {
    return item => Object.assign({}, { value: item[valueProperty], label: item[labelProperty] });
}

export function CamelCaseToWords( text ) {
    const result = text.replace(/([A-Z])/g, " $1");
    return result.charAt(0).toUpperCase() + result.slice(1);
}