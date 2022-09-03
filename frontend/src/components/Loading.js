import React from 'react';
import {Spinner} from "react-bootstrap";

export default function Loading({ show, block = true, size = "xl" }) {
    const spinner = <span className="me-2"><Spinner
        as="span"
        animation="border"
        role="status"
        size={size}
        aria-hidden="true"
    /></span>;

    if ( ! show ) {
        return null;
    }

    return block ? <div className="p-5">{spinner}</div> : spinner;
}