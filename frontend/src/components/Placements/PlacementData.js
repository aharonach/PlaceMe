import React from 'react';
import RecordDetails from "../RecordDetails";
import {useOutletContext} from "react-router-dom";
import ResultData from "./Results/ResultData";
import {Card} from "react-bootstrap";

export default function PlacementData(){
    const { placement } = useOutletContext();
    const details = [
        { label: "Number Of Classes", value: placement.numberOfClasses },
        { label: "Group", value: placement.groupId ? placement.group.name : "No group" },
        { label: "Created On", value: placement.createdTime },
    ];

    return (
        <>
            <RecordDetails details={details} />
            {placement?.selectedResult ? (
                <Card as="article">
                    <Card.Body>
                        <h3>Selected Result</h3>
                        <ResultData externalResult={placement.selectedResult} />
                    </Card.Body>
                </Card>
            ) : null}
        </>
    );
}