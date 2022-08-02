import React from 'react';

export default function FieldFeedback({ hasError, error }) {
    let message = error?.message;

    if ( error && ! message ) {
        switch (error.type) {
            case 'required':
                message = "This field is required.";
                break;
            default:
                break;
        }
    }

    return (
        hasError && <div className="invalid-feedback">{message}</div>
    );
}