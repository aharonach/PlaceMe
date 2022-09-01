import {useOutletContext, useParams} from "react-router-dom";
import useFetchList from "../../../hooks/useFetchList";
import {Alert, Card, Col, Row, Stack} from "react-bootstrap";
import React from "react";
import Gender from "../../General/Gender";
import Loading from "../../Loading";
import {PeopleFill} from "react-bootstrap-icons";

export default function Classes() {
    const { placementId } = useParams();
    const { result } = useOutletContext();
    const [classes, error, loading, axiosFetch] = useFetchList({
        fetchUrl: `/placements/${placementId}/results/${result.id}/classes`,
        propertyName: "placementClassroomList"
    });

    let classNumber = 1;

    return (
        <>
            <h3>Classrooms</h3>
            <Loading show={loading} />
            {error && <Alert variant="danger">{error}</Alert> }
            {!loading && !error && <Row>
                {classes.map( classInfo => (
                    <Col key={classInfo.id} md={6} lg={3}>
                        <Card className="mb-2">
                            <Card.Header as={"h4"}>Class #{classNumber++}</Card.Header>
                            <Card.Body>
                                <Stack gap={2} direction="horizontal" className="mb-2">
                                    <><strong>Score:</strong> {classInfo.classScore.toFixed(2)}</>
                                    <div className="vr" />
                                    <Gender gender={"MALE"} >{classInfo.numberOfMales}</Gender>
                                    <Gender gender={"FEMALE"} >{classInfo.numberOfFemales}</Gender>
                                    <div className="vr" />
                                    <PeopleFill /> {classInfo.numOfPupils}
                                </Stack>
                                {classInfo?.pupils.map(pupil => (
                                    <div key={pupil.id}><Gender pill gender={pupil.gender} /> {pupil.firstName} {pupil.lastName}</div>
                                ))}
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>}
        </>
    )
}