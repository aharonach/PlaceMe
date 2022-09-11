// noinspection JSUnresolvedVariable

import {useParams} from "react-router-dom";
import useFetchList from "../../../hooks/useFetchList";
import {Alert, Button, Card, Col, Offcanvas, Row, Stack} from "react-bootstrap";
import React, {useState} from "react";
import Gender from "../../General/Gender";
import Loading from "../../Loading";
import {InfoCircleFill, PeopleFill} from "react-bootstrap-icons";
import {fixedNumber} from "../../../utils";
import useFetchRecord from "../../../hooks/useFetchRecord";
import "./Classes.scss";
import AttributeValues from "../../Pupils/AttributeValues";

export default function Classes({ result }) {
    const { placementId } = useParams();
    const [classrooms, error, loading] = useFetchList({
        fetchUrl: `/placements/${placementId}/results/${result.id}/classes`,
        propertyName: "placementClassroomList"
    });

    // eslint-disable-next-line no-unused-vars
    const [classesInfo] = useFetchRecord({
        fetchUrl: `/placements/${placementId}/results/${result.id}/classes/info`,
    });
    const [selectedPupil, setSelectedPupil] = useState();
    const [classView, setClassView] = useState('row');

    const handleClassView = () => {
        setClassView(classView === 'row' ? 'grid' : 'row');
    }

    let classNumber = 1;

    return (
        <>
            <h3>
                Classrooms
                <Button variant="link" onClick={handleClassView}>{classView === 'row' ? "View as scrollable row" : "View as grid"}</Button>
            </h3>
            <Loading show={loading} />
            {error && <Alert variant="danger">{error}</Alert>}
            {<Alert variant="secondary">
                {selectedPupil
                    ? <PupilData result={result} selected={selectedPupil} setSelected={setSelectedPupil} />
                    : <>Click on a pupil to view details</>}
            </Alert>}
            {<Alert variant="info"><Legend /></Alert>}
            {!loading && !error && classrooms && (
                <Row
                    lg={classView === 'grid' ? 3 : null}
                    xl={classView === 'grid' ? 4 : null}
                    className={classView === 'row' ? 'flex-nowrap overflow-auto' : ''}
                >
                    {classrooms.map(classroom => (
                        <Col key={classroom.id}>
                            <Card className="mb-2">
                                <Card.Header as={"h4"}>Class #{classNumber++}</Card.Header>
                                <Card.Body>
                                    <ClassData classInfo={classroom} view={classView} />
                                    <ClassPupils classroom={classroom} classInfo={classesInfo} selected={selectedPupil} setSelected={setSelectedPupil} />
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}
        </>
    )
}

const ClassData = ({classInfo, view}) => {
    const viewClass = view === 'grid' ? 'flex-wrap' : "";

    return (
        <Stack gap={2} direction="horizontal" className={`mb-2 ${viewClass}`}>
            <><strong>Score:</strong> {fixedNumber(classInfo.classScore)}</>
            <div className="vr" />
            <Gender gender={"MALE"} >{classInfo.numberOfMales}</Gender>
            <Gender gender={"FEMALE"} >{classInfo.numberOfFemales}</Gender>
            <div className="vr" />
            <PeopleFill /> {classInfo.numOfPupils}
        </Stack>
    )
}

const ClassPupils = ({classroom, classInfo, selected, setSelected}) => {
    const selectPupil = (pupil) => {
        if ( pupil.id === selected?.id ) {
            setSelected(null);
            return;
        }

        setSelected(pupil);
    };

    return (
        classroom?.pupils.map(pupil => {
            // Conditions
            const preferToBe = classInfo?.preferToBeList[selected?.id]?.includes( pupil.id );
            const preferNotToBe = classInfo?.preferNotToBeList[selected?.id]?.includes( pupil.id );
            const isAloneInClassroom = classInfo?.numberOfFriendsInClass[pupil.id] === 0;

            // Class names for styling
            const preferToBeClass = preferToBe ? "prefer-to-be" : "";
            const selectedIsNotWith = preferNotToBe ? "prefer-not-to-be" : "";
            const isAloneInClassroomClass = isAloneInClassroom ? "pupil-is-alone" : "";
            const pupilSelectedClass = selected?.id === pupil.id || preferNotToBe || preferToBe ? "fw-bold" : "link-dark";

            return (
                <Stack gap={2} direction={"horizontal"} key={pupil.id}>
                    <Gender pill gender={pupil.gender} noIcon/>{' '}
                    <Button
                        variant="link"
                        className={`p-0 pupil-in-class ${pupilSelectedClass} ${preferToBeClass} ${selectedIsNotWith} ${isAloneInClassroomClass}`}
                        onClick={() => selectPupil(pupil)}
                    >{pupil.firstName} {pupil.lastName}</Button>
                </Stack>
            )
        })
    );
}

const PupilData = ({ result, selected, setSelected }) => {
    const [show, setShow] = useState(false);

    const handleClick = (e) => {
        e.preventDefault();
        setSelected(null);
    };

    const handleClose = () => {
        setShow(false)
    };

    const handleShow = () => setShow(true);

    return (
        <>
            <Stack direction="horizontal" gap={3} className="align-items-center">
                <span>Showing info for: <strong>{selected.firstName} {selected.lastName} ({selected.givenId})</strong></span>
                <Button variant="secondary" size="sm" onClick={handleShow}>View attribute values</Button>
                <Button variant="link" size="sm" as="a" onClick={handleClick}>Clear selection</Button>
            </Stack>
            <Offcanvas show={show} onHide={handleClose} placement="end" scroll={true}>
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>{selected.firstName} {selected.lastName} ({selected.givenId})</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>{selected && <AttributeValues pupil={selected} group={result.group} />}</Offcanvas.Body>
            </Offcanvas>
        </>
    )
}

const Legend = () => {
    return (
        <Stack direction="horizontal" gap={3}>
            <InfoCircleFill />
            <span>Legend:</span>
            <span className="prefer-to-be">Prefer to be with</span>
            <span className="vr"></span>
            <span className="prefer-not-to-be">Prefer not to be with</span>
            <span className="vr"></span>
            <span className="pupil-is-alone">Alone in class</span>
        </Stack>
    )
}