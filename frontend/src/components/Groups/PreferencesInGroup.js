import React, {useMemo, useState} from 'react';
import {
    Alert,
    Badge,
    Button,
    Card,
    Col,
    Row,
    Stack,
    ToggleButton,
    ToggleButtonGroup
} from "react-bootstrap";
import {useOutletContext} from "react-router-dom";
import useFetchList from "../../hooks/useFetchList";
import Loading from "../Loading";
import {objectIsEmpty} from "../../utils";
import Api from "../../api";
import { X } from 'react-bootstrap-icons';

export default function PreferencesInGroup() {
    const {group} = useOutletContext();
    // eslint-disable-next-line no-unused-vars
    const [preferences, errorFetch, loadingFetch, axiosFetch, getPreferences] = useFetchList({
        fetchUrl: `/groups/${group.id}/preferences`,
        propertyName: "preferenceDtoList"
    });
    const [pupils, errorPupils, loadingPupils] = useFetchList({
        fetchUrl: `/groups/${group.id}/pupils/all`,
        propertyName: "pupilList"
    });
    const [selector, setSelector] = useState(0);
    const [selected, setSelected] = useState(0);
    const [wantsToBe, setWantsToBe] = useState('yes');

    const reset = () => {
        setSelector(0);
        setSelected(0);
        setWantsToBe('yes');
    };

    const handleToggle = (value) => {
        setWantsToBe(value);
    }

    const handleSave = async () => {
        const response = await Api.put(`/groups/${group.id}/preferences`, prepareData(selector, selected, wantsToBe));

        if ( response.status === 200 ) {
            getPreferences();
        }
    }

    const mapped = useMemo(() => mapPreferencesByPupils(preferences), [preferences]);

    return (
        <>
            {errorFetch && <Alert variant="danger">{errorFetch}</Alert> }
            <>
                <h3>Preferences</h3>
                <Instructions selector={selector} />
                <Row>
                    <Col md={4}>
                        <div className="mb-3">
                            <Button onClick={handleSave} disabled={!(selector && selected)}>Add Preference</Button>
                            <Loading show={loadingPupils} />
                        </div>
                        <div className="mb-3">
                            <ToggleButtons selector={selector} handleToggle={handleToggle} reset={reset} wantsToBe={wantsToBe} />
                        </div>
                        <Loading show={loadingFetch} block={false} size="sm" />
                        {!errorPupils && pupils && (
                            <div className="d-flex flex-wrap">
                                <PupilButtons
                                    pupils={pupils}
                                    selector={selector}
                                    selected={selected}
                                    setSelected={setSelected}
                                    setSelector={setSelector}
                                />
                            </div>
                        )}
                        <div className="mb-3">
                            <ToggleButtons selector={selector} handleToggle={handleToggle} reset={reset} wantsToBe={wantsToBe} />
                        </div>
                        <div className="mb-3">
                            <Button onClick={handleSave} disabled={!(selector && selected)}>Add Preference</Button>
                            <Loading show={loadingPupils} />
                        </div>
                    </Col>
                    <Col md={8}>
                        <Preferences group={group} items={mapped} updatePreferences={getPreferences} />
                    </Col>
                </Row>
            </>
        </>
    );
};

const ToggleButtons = ({ wantsToBe, handleToggle, selector, reset}) => {
    return (
        <>
            <ToggleButtonGroup type="radio" name="wantsToBeWith" value={wantsToBe} onChange={handleToggle}>
                <ToggleButton id="wantsToBeWith" value="yes" variant={buttonVariant(selector, wantsToBe, 'yes')} disabled={selector === 0}>Prefers</ToggleButton>
                <ToggleButton id="doesntWantToBeWith" value="no" variant={buttonVariant(selector, wantsToBe, 'no')} disabled={selector === 0}>Doesn't Prefer</ToggleButton>
            </ToggleButtonGroup>
            {selector ? <Button variant="link" onClick={reset}>Clear Selection</Button> : null}
        </>
    );
}

const Instructions = ({ selector }) => {
    let message = '';

    if ( ! selector ) {
        message = 'Step 1: Choose pupil';
    }

    if ( selector ) {
        message = 'Step 2: Choose his/her preference and then click save';
    }

    return message && <Alert variant="info">
        <Alert.Heading as="h5">Instructions</Alert.Heading>
        {message}
    </Alert>
}

const Preferences = ({ group, items, updatePreferences }) => {
    return (
        <Row xs={2} md={3} lg={4} className="g-2">
            {Object.keys(items).map(selectorId => (
                <Col key={selectorId}>
                    <Card className="h-100">
                        <Card.Body>
                            <Card.Subtitle className="border-bottom mb-1 pb-1">{`${items[selectorId].name} `}</Card.Subtitle>
                            <Stack direction="vertical" gap={2}>
                                {!objectIsEmpty(items[selectorId].yes) && (
                                    <Stack direction="vertical" gap={1}>
                                        <span className="text-success">Prefers:</span>
                                        <span>
                                            {Object.keys(items[selectorId].yes).map(selectedId => <Preference
                                                key={selectedId}
                                                groupId={group.id}
                                                selectorId={selectorId}
                                                selectedId={selectedId}
                                                selectedName={items[selectorId].yes[selectedId]}
                                                updatePreferences={updatePreferences}
                                            />)}
                                        </span>
                                    </Stack>
                                )}
                                {/** Show separator **/}
                                {!objectIsEmpty(items[selectorId].no) && (
                                    <Stack direction="vertical" gap={1}>
                                        <span className="text-danger">Doesn't prefer:</span><br />
                                        <span>{Object.keys(items[selectorId].no).map(selectedId => <Preference
                                                key={selectedId}
                                                groupId={group.id}
                                                selectorId={selectorId}
                                                selectedId={selectedId}
                                                selectedName={items[selectorId].no[selectedId]}
                                                updatePreferences={updatePreferences}
                                        />)}</span>
                                    </Stack>
                                )}
                            </Stack>
                        </Card.Body>
                    </Card>
                </Col>
            ))}
        </Row>
    );
};

const Preference = ({groupId, selectorId, selectedId, selectedName, updatePreferences}) => {
    const handleDelete = async (e) => {
        e.preventDefault();

        const response = await Api.delete(
            `/groups/${groupId}/preferences`,
            { data: prepareData(selectorId, selectedId) });

        if ( response.status === 200 ) {
            updatePreferences();
        }
    };

    return (
        <Badge key={selectedId} className="me-1 mb-1 d-inline-flex align-items-center text-wrap" size="sm">
            <Button variant="link" onClick={handleDelete} title="Delete Preference" className="lh-1 p-0">
                <X color="white" size={20} aria-hidden="true" />
            </Button> {selectedName}
        </Badge>
    )
}

const PupilButtons = ({ pupils, selector, setSelector, selected, setSelected }) => {
    return pupils.map( pupil => (
        <span key={pupil.id} className="d-inline-block mb-1 me-1">
            {!selector
                ? <ToggleButton
                    size="sm"
                    type="radio"
                    variant="outline-primary"
                    id={`selector-${pupil.id}`}
                    value={pupil.id}
                    onChange={(e) => setSelector(parseInt(e.target.value))}
                    disabled={selector && selector !== pupil.id}
                    checked={selector === pupil.id}
                >{pupil.firstName} {pupil.lastName}</ToggleButton>
                : <ToggleButton
                    size="sm"
                    type="radio"
                    variant={selector === pupil.id ? 'primary' : 'outline-primary'}
                    id={`selected-${pupil.id}`}
                    value={pupil.id}
                    onChange={(e) => setSelected(parseInt(e.target.value))}
                    disabled={selector === pupil.id}
                    checked={selected === pupil.id}
                >{pupil.firstName} {pupil.lastName}</ToggleButton>}
        </span>
    ));
}

/**
 * Map the preferences by pupil selector ID
 * @param preferences
 * @returns object:
 *  {
 *      selectorId: {
 *          name: [string],
 *          yes: { selectorId: [string], [...]},
 *          no: { selectorId: [string], [...]}
 *      }
 *      ...
 *  }
 */
const mapPreferencesByPupils = (preferences) => {
    const map = {};

    preferences?.forEach( preference => {
        const selectorId = preference.selectorSelectedId.selectorId;
        const selectedId = preference.selectorSelectedId.selectedId;
        const wantsToBe  = preference.isSelectorWantToBeWithSelected;

        // initialize an array for selector pupil with his name and map of selected pupils.
        if ( ! map[selectorId] ) {
            map[selectorId] = {
                name: `${preference.selectorFirstName} ${preference.selectorLastName}`,
                yes:  {},
                no:   {}
            };
        }

        map[selectorId][wantsToBe ? 'yes' : 'no'][selectedId] = `${preference.selectedFirstName} ${preference.selectedLastName}`;
    });

    return map;
};

const buttonVariant = (selected, wantsToBe, equals) => {
    if ( selected && wantsToBe ) {
        if ( wantsToBe === 'yes' && equals === 'yes' ) {
            return 'success';
        }

        if ( wantsToBe === 'no' && equals === 'no' ) {
            return 'danger';
        }
    }

    return 'outline-secondary';
};

const prepareData = (selector, selected, wantsToBe) => {
    const data = {
        selectorSelectedId: {
            selectorId: selector,
            selectedId: selected,
        }
    };

    if ( wantsToBe ) {
        data['isSelectorWantToBeWithSelected'] = wantsToBe === 'yes';
    }

    return data;
}