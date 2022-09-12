// noinspection JSUnresolvedVariable

import {Link, useParams} from "react-router-dom";
import useFetchList from "../../../hooks/useFetchList";
import {Alert, Button, Card, Col, Offcanvas, OverlayTrigger, Popover, Row, Stack} from "react-bootstrap";
import React, {useContext, useState} from "react";
import Gender from "../../General/Gender";
import {InfoCircleFill, PeopleFill} from "react-bootstrap-icons";
import {fixedNumber} from "../../../utils";
import useFetchRecord from "../../../hooks/useFetchRecord";
import "./Classes.scss";
import AttributeValues from "../../Pupils/AttributeValues";
import {useForm} from "react-hook-form";
import HtmlForm from "../../Forms/HtmlForm";

const ResultContext = React.createContext({});

export default function Classes({ result }) {
    const { placementId } = useParams();
    const [classrooms, error, loading, fetchClasses] = useFetchList({
        fetchUrl: `/placements/${placementId}/results/${result.id}/classes`,
        propertyName: "placementClassroomList"
    });

    // eslint-disable-next-line no-unused-vars
    const [classesInfo, errorInfo, loadingInfo, fetchInfo, getClassInfo] = useFetchRecord({
        fetchUrl: `/placements/${placementId}/results/${result.id}/classes/info`,
    });
    const [selectedPupil, setSelectedPupil] = useState();
    const [selectedClassroom, setSelectedClassroom] = useState();
    const [classView, setClassView] = useState('row');

    const handleClassView = () => {
        setClassView(classView === 'row' ? 'grid' : 'row');
    }

    return (
        <ResultContext.Provider value={{ getClassInfo, result, selectedPupil, setSelectedPupil, selectedClassroom, setSelectedClassroom, classrooms, classesInfo, fetchClasses }}>
            <h3>
                Classrooms
                <Button variant="link" onClick={handleClassView}>
                    {classView === 'row' ? "View as scrollable row" : "View as grid"}
                </Button>
                <Legend />
            </h3>
            {error && <Alert variant="danger">{error}</Alert>}
            {<Alert variant="secondary">
                {selectedPupil
                    ? <PupilData />
                    : <>Click on a pupil to view details</>}
            </Alert>}
            <Row
                lg={classView === 'grid' ? 3 : null}
                xl={classView === 'grid' ? 4 : null}
                className={classView === 'row' ? 'flex-nowrap overflow-auto' : ''}
            >
                {classrooms?.map((classroom, i) => (
                    <Classroom
                        key={classroom.id}
                        classNumber={i+1}
                        classroom={classroom}
                        classView={classView}
                    />
                ))}
            </Row>
        </ResultContext.Provider>
    )
}

const Classroom = ({ classNumber, classroom, classView }) => {
    const {
        result,
        selectedPupil,
        fetchClasses,
        selectedClassroom,
        setSelectedClassroom,
        getClassInfo
    } = useContext(ResultContext);
    const { placementId } = useParams();

    const handleMove = async () => {
        const response = await fetchClasses({
            method: 'post',
            url: `/placements/${placementId}/results/${result.id}/classes`,
            data: { pupilId: selectedPupil.id, classroomFrom: selectedClassroom, classroomTo: classroom.id }
        });

        if ( response ) {
            setSelectedClassroom(classroom.id);
            getClassInfo();
        }
    }

    return (
        <Col key={classroom.id}>
            <Card className="mb-2 shadow-sm">
                <Card.Header as={"h4"}>
                    Class #{classNumber}
                    {selectedPupil && selectedClassroom !== classroom.id && <Button size="sm" onClick={handleMove} className="ms-3">Move Here</Button>}
                </Card.Header>
                <Card.Body>
                    <ClassData classroom={classroom} view={classView} />
                    <ClassPupils classroom={classroom} />
                </Card.Body>
            </Card>
        </Col>
    );
}

const ClassData = ({ classroom, view }) => {
    const viewClass = view === 'grid' ? 'flex-wrap' : "";

    return (
        <Stack gap={2} direction="horizontal" className={`mb-2 ${viewClass}`}>
            <><strong>Score:</strong> {classroom?.classScoreForUi ? fixedNumber(classroom?.classScoreForUi) : null}</>
            <div className="vr" />
            <Gender gender={"MALE"} >{classroom?.numberOfMales}</Gender>
            <Gender gender={"FEMALE"} >{classroom?.numberOfFemales}</Gender>
            <PeopleFill /> {classroom?.numOfPupils}
        </Stack>
    )
}

const ClassPupils = ({ classroom }) => {
    const {
        classesInfo,
        selectedPupil: selected,
        setSelectedPupil: setSelected,
        setSelectedClassroom,
    } = useContext(ResultContext);

    const selectPupil = (pupil) => {
        if ( pupil.id === selected?.id ) {
            setSelected(null);
            return;
        }

        setSelected(pupil);
        setSelectedClassroom(classroom.id);
    };

    return (
        <>
            {classroom?.pupils.map(pupil => {
                // Conditions
                const preferToBe = classesInfo?.preferToBeList[selected?.id]?.includes( pupil.id );
                const preferNotToBe = classesInfo?.preferNotToBeList[selected?.id]?.includes( pupil.id );
                const isAloneInClassroom = classesInfo?.numberOfFriendsInClass[pupil.id] === 0 && classesInfo?.preferToBeList[pupil.id]?.length > 0;

                // Class names for styling
                const preferToBeClass = preferToBe ? "prefer-to-be" : "";
                const selectedIsNotWith = preferNotToBe ? "prefer-not-to-be" : "";
                const isAloneInClassroomClass = isAloneInClassroom ? "pupil-is-alone" : "";
                const pupilSelectedClass = selected?.id === pupil.id || preferNotToBe || preferToBe ? "selected-pupil" : "link-dark";

                return (
                    <Stack gap={2} direction={"horizontal"} key={pupil.id}>
                        <Gender pill gender={pupil.gender} noIcon/>{' '}
                        <Button
                            variant="link"
                            className={`py-0 pupil-in-class ${pupilSelectedClass} ${preferToBeClass} ${selectedIsNotWith} ${isAloneInClassroomClass}`}
                            onClick={() => selectPupil(pupil)}
                        >{pupil.firstName} {pupil.lastName}</Button>
                    </Stack>
                )
            })}
        </>
    );
}

const PupilData = () => {
    const { result, selectedPupil, setSelectedPupil } = useContext(ResultContext);
    const [show, setShow] = useState(false);

    const handleClick = (e) => {
        e.preventDefault();
        setSelectedPupil(null);
    };

    const handleClose = () => {
        setShow(false)
    };

    const handleShow = () => setShow(true);

    return (
        <>
            <Stack direction="horizontal" gap={3} className="align-items-center">
                <span>Showing info for: <Link to={`/pupils/${selectedPupil.id}`}><strong>{selectedPupil.firstName} {selectedPupil.lastName} ({selectedPupil.givenId})</strong></Link></span>
                <Button variant="secondary" size="sm" onClick={handleShow}>View attribute values</Button>
                <Button variant="link" size="sm" as="a" onClick={handleClick}>Clear selection</Button>
            </Stack>
            <Offcanvas show={show} onHide={handleClose} placement="end" scroll={true}>
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>{selectedPupil.firstName} {selectedPupil.lastName} ({selectedPupil.givenId})</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>{selectedPupil && <AttributeValues pupil={selectedPupil} group={result.group} />}</Offcanvas.Body>
            </Offcanvas>
        </>
    )
}

const Legend = () => {
    const popover = (
            <Popover>
                <Popover.Header as="h3">Legend</Popover.Header>
                <Popover.Body>
                    <div className="prefer-to-be">Prefer to be with</div>
                    <div className="prefer-not-to-be">Prefer not to be with</div>
                    <div className="pupil-is-alone">Alone in class</div>
                </Popover.Body>
            </Popover>
        );

    return (
        <OverlayTrigger trigger="click" placement="top" overlay={popover}>
            <Button variant="info" size="sm"><InfoCircleFill /> Legend</Button>
        </OverlayTrigger>
    )
}