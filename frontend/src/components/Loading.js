import React from 'react';
import {Spinner} from "react-bootstrap";

export default function Loading({ show, size = 'xl' }) {
    const spinner = <Spinner
        as="span"
        animation="border"
        role="status"
        size={size}
        aria-hidden="true"
    />;

    return show && spinner;
}