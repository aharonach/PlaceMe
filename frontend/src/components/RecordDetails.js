import React from 'react';
import {Col, Row} from "react-bootstrap";

export default function RecordDetails({ details, numOfColumns = 2, children }) {
    const colSize = (12 / numOfColumns) % 2 === 0 ? 12 / numOfColumns : null;

    return (
        <>
            {children}
            <Row>
                {details.map(detail => (
                        <Col key={detail.label} md={colSize} className="mb-3">
                            <strong className="d-block">{detail.label}</strong>
                            <span className="d-block">{detail.value}</span>
                        </Col>
                    )
                )}
            </Row>
        </>
    )
}