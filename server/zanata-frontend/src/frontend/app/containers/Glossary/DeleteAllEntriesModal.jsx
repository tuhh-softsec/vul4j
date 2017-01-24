import React, { Component, PropTypes } from 'react'
import ReactDOM from 'react-dom'
import { LoaderText, Icon } from '../../components'
import { Button, Tooltip, Overlay } from 'react-bootstrap'

/**
 * Confirmation modal dialog for delete all glossary entries
 */
class DeleteAllEntriesModal extends Component {
  render () {
    const {
      show,
      isDeleting,
      handleDeleteAllEntriesDisplay,
      handleDeleteAllEntries
    } = this.props

    /* eslint-disable react/jsx-no-bind */
    return (
      <div className='block-inline'>
        <Overlay
          placement='bottom'
          target={() => ReactDOM.findDOMNode(this)}
          rootClose
          show={show}
          onHide={() => handleDeleteAllEntriesDisplay(false)}>
          <Tooltip id='delete-entries' title='Delete all glossary entries'>
            <p>
              Are you sure you want to delete&nbsp;
              <strong>all entries</strong>&nbsp;?
            </p>
            <span className='button-spacing'>
              <Button bsStyle='default'
                onClick={() => handleDeleteAllEntriesDisplay(false)}>
                Cancel
              </Button>
              <Button bsStyle='danger' type='button'
                disabled={isDeleting}
                onClick={() => handleDeleteAllEntries()}>
                <LoaderText loading={isDeleting} size='n1'
                  loadingText='Deleting'>
                  Delete
                </LoaderText>
              </Button>
            </span>
          </Tooltip>
        </Overlay>
        <Button bsStyle='link' type='button'
          onClick={() => handleDeleteAllEntriesDisplay(true)}
          disabled={isDeleting}>
          <span>
            <Icon name='trash' className='deleteicon s1' />
            <span className='hidden-lesm'>Delete</span>
          </span>
        </Button>
      </div>
    )
    /* eslint-enable react/jsx-no-bind */
  }
}

DeleteAllEntriesModal.propTypes = {
  show: React.PropTypes.bool,
  isDeleting: React.PropTypes.bool,
  handleDeleteAllEntriesDisplay: PropTypes.func.isRequired,
  handleDeleteAllEntries: React.PropTypes.func.isRequired
}

export default DeleteAllEntriesModal
